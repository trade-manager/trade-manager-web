package org.trade.ui.strategy;

import de.sciss.syntaxpane.DefaultSyntaxKit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.core.broker.IBrokerModel;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.DynamicCode;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.BarSize;
import org.trade.core.persistent.IPersistentModel;
import org.trade.core.persistent.dao.Rule;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.strategy.IStrategyRule;
import org.trade.core.TradeAppLoadConfig;
import org.trade.base.StreamEditorPane;
import org.trade.ui.persistent.TradestrategyBase;

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
import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 *
 */
public class StrategyPanelTest {

    private final static Logger _log = LoggerFactory.getLogger(StrategyPanelTest.class);

    private IPersistentModel tradePersistentModel = null;
    private Tradestrategy tradestrategy = null;
    private String m_templateName = null;
    private String m_strategyDir = null;
    private final String m_tmpDir = "temp";

    /**
     * Method setUpBeforeClass.
     */
    @BeforeAll
    public static void setUpBeforeClass() {
    }

    /**
     * Method setUp.
     */
    @BeforeEach
    public void setUp() throws Exception {

        TradeAppLoadConfig.loadAppProperties();
        m_templateName = ConfigProperties.getPropAsString("trade.strategy.template");
        assertNotNull(m_templateName);
        m_strategyDir = ConfigProperties.getPropAsString("trade.strategy.default.dir");
        assertNotNull(m_strategyDir);
        this.tradePersistentModel = (IPersistentModel) ClassFactory
                .getServiceForInterface(IPersistentModel._persistentModel, this);
        String symbol = "TEST";
        this.tradestrategy = TradestrategyBase.getTestTradestrategy(symbol);
        assertNotNull(this.tradestrategy);
        List<Strategy> strategies = this.tradePersistentModel.findStrategies();
        assertNotNull(strategies);
        for (Strategy strategy : strategies) {
            String fileName = m_strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/') + strategy.getClassName()
                    + ".java";
            String content = readFile(fileName);
            assertNotNull("setUp: Strategy java file should be not null", content);
            if (strategy.getRules().isEmpty()) {
                Rule nextRule = new Rule(strategy, 1, null, TradingCalendar.getDateTimeNowMarketTimeZone(),
                        content.getBytes(), TradingCalendar.getDateTimeNowMarketTimeZone());
                strategy.add(nextRule);
                this.tradePersistentModel.persistAspect(nextRule);
            }
        }
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        File dir = new File(m_tmpDir);
        StrategyPanel.deleteDir(dir);
        TradestrategyBase.clearDBData();
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void testJEditorPaneTextEquals() throws Exception {

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

        String fileName = m_strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/') + m_templateName + ".java";
        String content = readFile(fileName);
        sourceText.setText(content);
        assertEquals(content,
                sourceText.getText());
        writeFile(fileName, content);
        String content1 = readFile(fileName);
        sourceText.setText(null);
        sourceText.setText(content1);
        assertEquals(
                content1, sourceText.getText());
    }

    @Test
    public void testDoCompileAndRunStrategy() throws Exception {

        IBrokerModel m_brokerManagerModel = (IBrokerModel) ClassFactory
                .getServiceForInterface(IBrokerModel._brokerTest, this);

        Vector<Object> parm = new Vector<>(0);
        parm.add(m_brokerManagerModel);
        parm.add(this.tradestrategy.getStrategyData());
        parm.add(this.tradestrategy.getId());
        _log.info("Ready to create Strategy");
        DynamicCode dynacode = new DynamicCode();
        dynacode.addSourceDir(new File(m_strategyDir));
        IStrategyRule strategyProxy = (IStrategyRule) dynacode.newProxyInstance(IStrategyRule.class,
                IStrategyRule.PACKAGE + m_templateName, parm);
        _log.info("Created Strategy{}", strategyProxy);
        strategyProxy.execute();

        while (!strategyProxy.isWaiting()) {
            Thread.sleep(250);
        }

        StrategyData.doDummyData(tradestrategy.getStrategyData().getBaseCandleSeries(),
                Tradingday.newInstance(TradingCalendar.getDateTimeNowMarketTimeZone()), 1, BarSize.FIVE_MIN, true,
                250);
        assertFalse(
                tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty());
        strategyProxy.cancel();
    }

    @Test
    public void testDoCompileRule() throws Exception {
        File srcDirFile;

        IBrokerModel m_brokerManagerModel = (IBrokerModel) ClassFactory
                .getServiceForInterface(IBrokerModel._brokerTest, this);

        Vector<Object> parm = new Vector<>(0);
        parm.add(m_brokerManagerModel);
        parm.add(this.tradestrategy.getStrategyData());
        parm.add(this.tradestrategy.getId());
        Strategy strategy = this.tradePersistentModel
                .findStrategyById(this.tradestrategy.getStrategy().getId());
        Integer version = this.tradePersistentModel.findRuleByMaxVersion(strategy);
        Rule myRule = null;
        for (Rule rule : strategy.getRules()) {
            if (version.equals(rule.getVersion()))
                myRule = rule;
        }
        assertNotNull(myRule);
        String fileDir = m_tmpDir + "/" + IStrategyRule.PACKAGE.replace('.', '/');
        String className = strategy.getClassName() + ".java";

        srcDirFile = new File(fileDir);
        srcDirFile.mkdirs();
        srcDirFile.deleteOnExit();
        FileWriter fileWriter = new FileWriter(fileDir + className);
        PrintWriter writer = new PrintWriter(fileWriter);
        writer.println(new String(myRule.getRule()));
        writer.flush();
        writer.close();
        fileWriter.close();

        _log.info("Ready to create Strategy");
        DynamicCode dynacode = new DynamicCode();
        dynacode.addSourceDir(new File(m_tmpDir));
        IStrategyRule strategyRule = (IStrategyRule) dynacode.newProxyInstance(IStrategyRule.class,
                IStrategyRule.PACKAGE + strategy.getClassName(), parm);
        assertNotNull(strategyRule);
    }

    @Test
    public void testDoCompile() throws Exception {

        StrategyPanel strategyPanel = new StrategyPanel(this.tradePersistentModel);
        List<Strategy> strategies = this.tradePersistentModel.findStrategies();
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
            myrule.setVersion(0);
            myrule.setStrategy(strategy);

        } else {
            myrule.setVersion(myrule.getVersion() + 1);
            myrule.setId(null);
        }
        assertNotNull(myrule);
        strategyPanel.doCompile(myrule);
    }

    @Test
    public void testDoSave() throws Exception {

        StrategyPanel strategyPanel = new StrategyPanel(this.tradePersistentModel);
        List<Strategy> strategies = this.tradePersistentModel.findStrategies();
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
            myrule.setVersion(0);
            myrule.setStrategy(strategy);

        } else {
            myrule.setVersion(myrule.getVersion() + 1);
            myrule.setId(myrule.getId());
        }
        myrule.setComment("Test Ver: " + myrule.getVersion());
        myrule.setCreateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
        StreamEditorPane textArea = new StreamEditorPane("text/rtf");
        new JScrollPane(textArea);
        String fileDir = m_strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/');
        String className = strategy.getClassName() + ".java";
        String fileName = fileDir + className;
        String content = strategyPanel.readFile(fileName);
        textArea.setText(content);
        myrule.setRule(textArea.getText().getBytes());
        myrule = this.tradePersistentModel.persistAspect(myrule);
        assertNotNull(myrule.getId());
        Rule ruleSaved = this.tradePersistentModel.findRuleById(myrule.getId());
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
    private String readFile(String fileName) throws Exception {

        FileReader inputStreamReader;
        BufferedReader bufferedReader;
        inputStreamReader = new FileReader(fileName);
        bufferedReader = new BufferedReader(inputStreamReader);
        String newLine = "\n";
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append(newLine);
        }

        bufferedReader.close();

        inputStreamReader.close();
        return sb.toString();
    }

    /**
     * Method writeFile.
     *
     * @param fileName String
     * @param content  String
     */
    private void writeFile(String fileName, String content) throws IOException {

        OutputStream out = new FileOutputStream(fileName);
        out.write(content.getBytes());
        out.flush();
        out.close();
    }
}
