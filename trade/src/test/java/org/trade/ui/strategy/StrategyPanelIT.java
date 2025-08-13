package org.trade.ui.strategy;

import de.sciss.syntaxpane.DefaultSyntaxKit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.base.StreamEditorPane;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.broker.IBrokerModel;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.ServiceException;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Rule;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.strategy.IStrategyRule;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.DynamicCode;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.ContentType;
import org.trade.core.valuetype.Side;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class StrategyPanelIT {

    private final static Logger _log = LoggerFactory.getLogger(StrategyPanelIT.class);

    @Autowired
    private TradeService tradeService;

    private static Tradestrategy tradestrategy;
    private static final String symbol = "IBM-" + TradestrategyBase.getRandomNumber(4);
    private static String templateName;
    private static String strategyDir;
    private static final String tmpDir = "../temp";

    /**
     * Method setUpBeforeClass.
     */
    @BeforeAll
    public static void setUpBeforeClass() {

        System.setProperty("java.awt.headless", "false");
    }

    /**
     * Method setUp.
     */
    @BeforeEach
    public void setUp() throws Exception {

        templateName = ConfigProperties.getPropAsString("trade.strategy.template");
        assertNotNull(templateName);
        strategyDir = ConfigProperties.getPropAsString("trade.strategy.default.dir");
        assertNotNull(strategyDir);
        // tradestrategy = TradestrategyBase.createTestTradestrategy(tradeService, symbol);
        tradestrategy = TradestrategyBase.createTestTradestrategy(tradeService, symbol, Side.BOT, ChartDays.ONE_DAY, BarSize.HOUR_MIN);

        assertNotNull(tradestrategy);
        Strategy strategy = tradeService.findStrategyById(tradestrategy.getStrategy().getId());
        String fileName = strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/') + strategy.getClassName()
                + ".java";
        String content = TradestrategyBase.readFile(fileName);

        if (null != content && strategy.getRules().isEmpty()) {

            Rule nextRule = new Rule(strategy, 1, null,
                    content.getBytes(), ContentType.JAVA);
            strategy.getRules().add(nextRule);
            strategy = this.tradeService.saveAspect(strategy);
        }
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        File dir = new File(tmpDir);
        StrategyPanel.deleteDir(dir);
        List<Strategy> strategies = this.tradeService.findStrategies();
        assertNotNull(strategies);

        for (Strategy strategy : strategies) {

            if (!strategy.getRules().isEmpty()) {

                strategy.getRules().clear();
                strategy = this.tradeService.saveAspect(strategy);
            }
        }

        TradestrategyBase.clearDBData(tradeService, tradestrategy);
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void checkStrategyFileEqualsRuleContent() {

        DefaultSyntaxKit.initKit();
        JEditorPane sourceText = new JEditorPane();
        JScrollPane jScrollPane = new JScrollPane(sourceText);
        jScrollPane.setEnabled(true);
        sourceText.setContentType(ContentType.JAVA);
        sourceText.setFont(new Font("monospaced", Font.PLAIN, 12));
        sourceText.setBackground(Color.white);
        sourceText.setForeground(Color.black);
        sourceText.setSelectedTextColor(Color.black);
        sourceText.setSelectionColor(Color.red);
        sourceText.setEditable(true);

        String fileName = strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/') + templateName + ".java";
        String content = TradestrategyBase.readFile(fileName);
        assertNotNull(content);
        sourceText.setText(content);
        assertEquals(content,
                sourceText.getText());
        TradestrategyBase.writeFile(fileName, content);
        String content1 = TradestrategyBase.readFile(fileName);
        sourceText.setText(null);
        sourceText.setText(content1);
        assertEquals(content1, sourceText.getText());
    }

    @Test
    public void doCompileAndRunStrategy() throws Exception {

        List<Object> param = new ArrayList<>();
        param.add(tradeService);
        IBrokerModel brokerManagerModel = (IBrokerModel) ClassFactory
                .getServiceForInterface(IBrokerModel._brokerTest, param, this);

        param.clear();
        param.add(this.tradeService);
        param.add(brokerManagerModel);
        param.add(tradestrategy.getStrategyData());
        param.add(tradestrategy.getId());
        _log.info("Ready to create Strategy");
        DynamicCode dynacode = new DynamicCode();
        dynacode.addSourceDir(new File(strategyDir));
        IStrategyRule strategyProxy = null;

        try {

            strategyProxy = (IStrategyRule) dynacode.newProxyInstance(IStrategyRule.class,
                    IStrategyRule.PACKAGE + templateName, param);

            _log.info("Created Strategy{}", strategyProxy);
            strategyProxy.execute();

            while (!strategyProxy.isWaiting()) {

                Thread.sleep(250);
            }
        } catch (Exception ex) {

            fail("Failed to create strategyProxy msg: " + ex.getMessage());
        }

        try {

            tradestrategy.getStrategyData().populateCandleSeries(tradestrategy.getTradingday(), tradestrategy.getChartDays(), tradestrategy.getBarSize(), Side.BOT.equals(tradestrategy.getSide()),
                    250);
        } catch (ServiceException ex) {

            fail("Failed to create dummy data msg: " + ex.getMessage());
        }

        strategyProxy.cancel();
        assertFalse(tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty());
    }

    @Test
    public void doCompileRule() {

        List<Object> param = new ArrayList<>();
        param.add(tradeService);
        IBrokerModel brokerManagerModel = null;
        Strategy strategy = null;

        try {

            brokerManagerModel = (IBrokerModel) ClassFactory
                    .getServiceForInterface(IBrokerModel._brokerTest, param, this);
            strategy = this.tradeService
                    .findStrategyById(tradestrategy.getStrategy().getId());
        } catch (Exception ex) {

            fail("Failed to create broker msg: " + ex.getMessage());
        }
        String contentType = ContentType.JAVA;
        Integer version = 1;
        Rule latestRule = this.tradeService.findRuleByMaxVersion(strategy, contentType);

        if (null != latestRule) {

            version = latestRule.getRuleVersion();
        }

        Rule myRule = null;

        for (Rule rule : strategy.getRules()) {

            if (version.equals(rule.getRuleVersion())) {
                myRule = rule;
            }
        }

        assertNotNull(myRule);
        String fileDir = tmpDir + "/" + IStrategyRule.PACKAGE.replace('.', '/');
        String className = strategy.getClassName() + ".java";

        File srcDirFile = new File(fileDir);
        assertTrue(srcDirFile.mkdirs());
        srcDirFile.deleteOnExit();

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileDir + className))) {

            writer.println(new String(myRule.getRule()));
            writer.flush();
        } catch (IOException ex) {

            fail("Failed to create file writer msg: " + ex.getMessage());
        }

        _log.info("Ready to create Strategy");
        DynamicCode dynacode = new DynamicCode();
        assertTrue(dynacode.addSourceDir(new File(tmpDir)));
        IStrategyRule strategyRule = null;
        param.clear();
        param.add(tradeService);
        param.add(brokerManagerModel);
        param.add(tradestrategy.getStrategyData());
        param.add(tradestrategy.getId());

        try {

            strategyRule = (IStrategyRule) dynacode.newProxyInstance(IStrategyRule.class,
                    IStrategyRule.PACKAGE + strategy.getClassName(), param);
        } catch (Exception ex) {

            fail("Failed to create strategyRule msg: " + ex.getMessage());
        }
        assertNotNull(strategyRule);
    }

    @Test
    public void doCompile() {

        StrategyPanel strategyPanel = new StrategyPanel(this.tradeService);
        Rule latestRule = this.tradeService.findRuleByMaxVersion(tradestrategy.getStrategy(), ContentType.JAVA);

        assertNotNull(latestRule);
        String fileName = strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/') + tradestrategy.getStrategy().getClassName() + ".java";
        String content = TradestrategyBase.readFile(fileName);
        strategyPanel.setContent(content, ContentType.JAVA);

        assertNotNull(latestRule);
        boolean result = strategyPanel.doCompile(latestRule);
        strategyPanel.doWindowClose();
        assertTrue(result);
    }

    @Test
    public void doSave() throws Exception {

        StrategyPanel strategyPanel = new StrategyPanel(this.tradeService);
        List<Strategy> strategies = this.tradeService.findStrategies();
        assertNotNull(strategies);
        assertFalse(strategies.isEmpty());

        Strategy strategy = strategies.getFirst();
        assertNotNull(strategy);
        Rule myrule = null;

        strategy.getRules().sort(Rule.VERSION_ORDER);

        for (Rule rule : strategy.getRules()) {

            myrule = rule;
        }

        if (null == myrule) {

            myrule = new Rule();
            myrule.setStrategy(strategy);
        }
        myrule.setComment("Test Ver: " + myrule.getRuleVersion());
        StreamEditorPane textArea = new StreamEditorPane(ContentType.TEXT);
        new JScrollPane(textArea);
        String fileDir = strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/');
        String className = strategy.getClassName() + ".java";
        String fileName = fileDir + className;
        String content = strategyPanel.readFile(fileName);
        textArea.setText(content);
        myrule.setRule(textArea.getText().getBytes());
        myrule = this.tradeService.saveAspect(myrule);
        assertNotNull(myrule.getId());
        Rule ruleSaved = this.tradeService.findRuleById(myrule.getId());
        assertNotNull(ruleSaved.getId());

        String javaCode = new String(ruleSaved.getRule());
        assertEquals(javaCode, textArea.getText());
        _log.info("Java file to Saved: {}", javaCode);
    }
}
