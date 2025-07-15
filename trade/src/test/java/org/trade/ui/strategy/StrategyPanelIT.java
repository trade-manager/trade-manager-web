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
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.strategy.IStrategyRule;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.DynamicCode;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.BarSize;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
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
    private String templateName;
    private String strategyDir;
    private final String tmpDir = "../temp";

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
        tradestrategy = TradestrategyBase.createTestTradestrategy(tradeService, symbol);
        assertNotNull(tradestrategy);
        List<Strategy> strategies = this.tradeService.findStrategies();
        assertNotNull(strategies);

        for (Strategy strategy : strategies) {

            String fileName = strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/') + strategy.getClassName()
                    + ".java";
            String content = readFile(fileName);
            assertNotNull(content);

            if (strategy.getRules().isEmpty()) {

                Rule nextRule = new Rule(strategy, 1, null,
                        content.getBytes(), "test/java");
                strategy.add(nextRule);
                nextRule = this.tradeService.saveAspect(nextRule);
            }
        }
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        File dir = new File(tmpDir);
        StrategyPanel.deleteDir(dir);
        TradestrategyBase.clearDBData(tradeService, tradestrategy);

        List<Rule> rules = tradeService.findRulesAll();

        for (Rule rule : rules) {

            rule.setStrategy(null);
            rule = tradeService.saveAspect(rule);
            tradeService.deleteAspect(rule);
        }
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
        sourceText.setContentType("text/java");
        sourceText.setFont(new Font("monospaced", Font.PLAIN, 12));
        sourceText.setBackground(Color.white);
        sourceText.setForeground(Color.black);
        sourceText.setSelectedTextColor(Color.black);
        sourceText.setSelectionColor(Color.red);
        sourceText.setEditable(true);

        String fileName = strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/') + templateName + ".java";
        String content = readFile(fileName);
        assertNotNull(content);
        sourceText.setText(content);
        assertEquals(content,
                sourceText.getText());
        writeFile(fileName, content);
        String content1 = readFile(fileName);
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

            StrategyData.doDummyData(tradestrategy.getStrategyData().getBaseCandleSeries(),
                    Tradingday.newInstance(TradingCalendar.getDateTimeNowMarketTimeZone()), 1, BarSize.FIVE_MIN, true,
                    250);
        } catch (ServiceException ex) {

            fail("Failed to create dummy data msg: " + ex.getMessage());
        }
        assertFalse(
                tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty());
        strategyProxy.cancel();
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

        Rule latestRule = this.tradeService.findRuleByMaxVersion(strategy);
        Integer version = 0;

        if (null != latestRule) {

            version = latestRule.getVersion();
        }

        Rule myRule = null;

        for (Rule rule : strategy.getRules()) {

            if (version.equals(rule.getVersion())) {
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
        List<Strategy> strategies = this.tradeService.findStrategies();
        assertNotNull(strategies);
        assertFalse(strategies.isEmpty());

        Strategy strategy = strategies.getFirst();
        assertNotNull(strategy);
        Rule myrule = null;
        strategy.getRules().sort(Rule.VERSION_ORDER);

        for (Rule rule : strategy.getRules()) {

            myrule = rule;
            break;
        }

        if (null == myrule) {

            myrule = new Rule();
            myrule.setStrategy(strategy);

        }
        assertNotNull(myrule);
        strategyPanel.doCompile(myrule);
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
        myrule.setComment("Test Ver: " + myrule.getVersion());
        StreamEditorPane textArea = new StreamEditorPane("text/rtf");
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

    /**
     * Method readFile.
     *
     * @param fileName String
     * @return String
     */
    private String readFile(String fileName) {

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {

            String newLine = "\n";
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {

                sb.append(line).append(newLine);
            }
            return sb.toString();
        } catch (IOException ex) {

            fail("Failed to read file msg: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Method writeFile.
     *
     * @param fileName String
     * @param content  String
     */
    private void writeFile(String fileName, String content) {

        try (OutputStream out = new FileOutputStream(fileName)) {

            out.write(content.getBytes());
        } catch (IOException ex) {

            fail("Failed to write OutputStream msg: " + ex.getMessage());
        }
    }
}
