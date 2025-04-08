package org.trade.ui.strategy;

import de.sciss.syntaxpane.DefaultSyntaxKit;
import org.junit.*;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trade.broker.IBrokerModel;
import org.trade.core.factory.ClassFactory;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.DynamicCode;
import org.trade.core.util.TradingCalendar;
import org.trade.dictionary.valuetype.BarSize;
import org.trade.persistent.IPersistentModel;
import org.trade.persistent.dao.*;
import org.trade.persistent.dao.Rule;
import org.trade.strategy.IStrategyRule;
import org.trade.strategy.data.StrategyData;
import org.trade.ui.TradeAppLoadConfig;
import org.trade.ui.base.StreamEditorPane;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import static org.junit.Assert.*;

/**
 *
 */
public class StrategyPanelTest {

    private final static Logger _log = LoggerFactory.getLogger(StrategyPanelTest.class);
    @org.junit.Rule
    public TestName name = new TestName();

    private String symbol = "TEST";
    private IPersistentModel tradePersistentModel = null;
    private Tradestrategy tradestrategy = null;
    private String m_templateName = null;
    private String m_strategyDir = null;
    private String m_tmpDir = "temp";

    /**
     * Method setUpBeforeClass.
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * Method setUp.
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        try {
            TradeAppLoadConfig.loadAppProperties();
            m_templateName = ConfigProperties.getPropAsString("trade.strategy.template");
            assertNotNull("setUp: Strategy template should be not null", m_templateName);
            m_strategyDir = ConfigProperties.getPropAsString("trade.strategy.default.dir");
            assertNotNull("setUp: Strategy dir should be not null", m_strategyDir);
            this.tradePersistentModel = (IPersistentModel) ClassFactory
                    .getServiceForInterface(IPersistentModel._persistentModel, this);
            this.tradestrategy = TradestrategyTest.getTestTradestrategy(symbol);
            assertNotNull("setUp: tradestrategy should be not null", this.tradestrategy);
            List<Strategy> strategies = this.tradePersistentModel.findStrategies();
            assertNotNull("setUp: Strategy should be not null", strategies);
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
        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }

    /**
     * Method tearDown.
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        File dir = new File(m_tmpDir);
        StrategyPanel.deleteDir(dir);
        TradestrategyTest.clearDBData();
    }

    /**
     * Method tearDownAfterClass.
     *
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void testJEditorPaneTextEquals() {

        try {
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
            assertEquals("testJEditorPaneTextEquals:  Strategy java file not equal to test source", content,
                    sourceText.getText());
            writeFile(fileName, content);
            String content1 = readFile(fileName);
            sourceText.setText(null);
            sourceText.setText(content1);
            assertEquals("testJEditorPaneTextEquals:  Strategy java file not equal to test source after write",
                    content1, sourceText.getText());
        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }

    /**
     * Method readFile.
     *
     * @param fileName String
     * @return String
     * @throws IOException
     */
    private String readFile(String fileName) throws IOException {
        FileReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        inputStreamReader = new FileReader(fileName);
        bufferedReader = null;
        bufferedReader = new BufferedReader(inputStreamReader);
        String newLine = "\n";
        StringBuffer sb = new StringBuffer();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line + newLine);
        }

        if (null != bufferedReader)
            bufferedReader.close();

        if (null != inputStreamReader)
            inputStreamReader.close();
        return sb.toString();
    }

    /**
     * Method writeFile.
     *
     * @param fileName String
     * @param content  String
     * @throws IOException
     */
    private void writeFile(String fileName, String content) throws IOException {

        OutputStream out = new FileOutputStream(fileName);
        out.write(content.getBytes());
        out.flush();
        out.close();
    }

    @Test
    public void testDoCompileAndRunStrategy() {
        try {
            IBrokerModel m_brokerManagerModel = (IBrokerModel) ClassFactory
                    .getServiceForInterface(IBrokerModel._brokerTest, this);

            Vector<Object> parm = new Vector<Object>(0);
            parm.add(m_brokerManagerModel);
            parm.add(this.tradestrategy.getStrategyData());
            parm.add(this.tradestrategy.getId());
            _log.info("Ready to create Strategy");
            DynamicCode dynacode = new DynamicCode();
            dynacode.addSourceDir(new File(m_strategyDir));
            IStrategyRule strategyProxy = (IStrategyRule) dynacode.newProxyInstance(IStrategyRule.class,
                    IStrategyRule.PACKAGE + m_templateName, parm);
            _log.info("Created Strategy" + strategyProxy);
            strategyProxy.execute();

            while (!strategyProxy.isWaiting()) {
                Thread.sleep(250);
            }

            StrategyData.doDummyData(tradestrategy.getStrategyData().getBaseCandleSeries(),
                    Tradingday.newInstance(TradingCalendar.getDateTimeNowMarketTimeZone()), 1, BarSize.FIVE_MIN, true,
                    250);
            assertFalse("testDoCompileAndRunStrategy: Base candle series is empty",
                    tradestrategy.getStrategyData().getBaseCandleSeries().isEmpty());
            strategyProxy.cancel();

        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }

    @Test
    public void testDoCompileRule() {
        File srcDirFile = null;
        try {
            IBrokerModel m_brokerManagerModel = (IBrokerModel) ClassFactory
                    .getServiceForInterface(IBrokerModel._brokerTest, this);

            Vector<Object> parm = new Vector<Object>(0);
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
            assertNotNull("testDoCompileRule: Rule should be not null", myRule);
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
            assertNotNull("testDoCompileRule: IStrategyRule should be not null", strategyRule);
        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }

    @Test
    public void testDoCompile() {
        try {
            StrategyPanel strategyPanel = new StrategyPanel(this.tradePersistentModel);
            List<Strategy> strategies = this.tradePersistentModel.findStrategies();
            assertNotNull("No strategies", strategies);
            assertEquals(false, strategies.isEmpty());

            Strategy strategy = strategies.get(0);
            assertNotNull("testDoCompile: Strategy should be not null", strategy);
            Rule myrule = null;

            Collections.sort(strategy.getRules(), Rule.VERSION_ORDER);

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
            assertNotNull("testDoCompile: Rule should be not null", myrule);
            strategyPanel.doCompile(myrule);
        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }

    @Test
    public void testDoSave() {
        try {
            StrategyPanel strategyPanel = new StrategyPanel(this.tradePersistentModel);
            List<Strategy> strategies = this.tradePersistentModel.findStrategies();
            assertNotNull("testDoSave: Strategies should be not null", strategies);
            assertEquals("testDoSave: Strategies should be not empty", false, strategies.isEmpty());

            Strategy strategy = strategies.get(0);
            assertNotNull("testDoSave: Strategy should be not null", strategy);
            Rule myrule = null;

            Collections.sort(strategy.getRules(), Rule.VERSION_ORDER);

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
            assertNotNull("testDoSave: Rule should be not null", myrule.getId());
            Rule ruleSaved = this.tradePersistentModel.findRuleById(myrule.getId());
            assertNotNull("testDoSave: Rule saved should be not null", ruleSaved.getId());
            String javaCode = new String(ruleSaved.getRule());
            assertEquals("testDoSave: Java rule test should be equals", javaCode, textArea.getText());
            _log.info("Java file to Saved: " + javaCode);
        } catch (Exception | AssertionError ex) {
            String msg = "Error running " + name.getMethodName() + " msg: " + ex.getMessage();
            _log.error(msg);
            fail(msg);
        }
    }
}
