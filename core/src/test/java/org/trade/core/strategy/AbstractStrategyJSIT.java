package org.trade.core.strategy;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.broker.IBrokerModel;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.dao.Rule;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradeOrderfill;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.strategy.IStrategyRule;
import org.trade.core.persistent.dao.strategy.StrategyRuleJS;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.ContentType;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.OrderStatus;
import org.trade.core.valuetype.Side;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class AbstractStrategyJSIT extends TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(AbstractStrategyJSIT.class);

    private static final String symbol = "TEST-" + TradestrategyBase.getRandomNumber(4);
    private static Tradestrategy tradestrategy;
    private static IBrokerModel brokerModel;
    private static String templateName;
    private static String strategyDir;
    private static IStrategyRule strategyProxy;
    private static boolean deleteAfter = true;

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

        List<Object> param = new ArrayList<>();
        param.add(tradeService);
        brokerModel = (IBrokerModel) ClassFactory.getServiceForInterface(IBrokerModel._brokerTest, param, this);
        templateName = ConfigProperties.getPropAsString("trade.strategy.template");
        strategyDir = ConfigProperties.getPropAsString("trade.strategy.default.dir");
        Integer clientId = ConfigProperties.getPropAsInt("trade.tws.clientId");
        Integer port = Integer.valueOf(ConfigProperties.getPropAsString("trade.tws.port"));
        String host = ConfigProperties.getPropAsString("trade.tws.host");
        brokerModel.onConnect(host, port, clientId);
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        if (deleteAfter) {

            brokerModel.onDisconnect();
            this.deleteRecords();
        }
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() throws Exception {

    }

    @Test
    @Order(100)
    public void fiveMinGapBarStrategyJS() throws Exception {

        Strategy strategy = (Strategy) DAOStrategy.newInstance().getObject();
        tradestrategy = this.createTestTradestrategy(strategy, symbol, Side.BOT, ChartDays.ONE_DAY, BarSize.FIVE_MIN);
        assertNotNull(tradestrategy);
        strategy = tradeService.findStrategyById(tradestrategy.getStrategy().getId());
        String fileName = strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/') + strategy.getClassName()
                + ".js";
        String content = TradestrategyBase.readFile(fileName);

        if (null != content && strategy.getRules().isEmpty()) {

            Rule nextRule = new Rule(strategy, true, 1, null,
                    content.getBytes(), ContentType.JAVASCRIPT);
            strategy.getRules().add(nextRule);
            strategy = this.tradeService.saveAspect(strategy);
        }

        strategyProxy = new StrategyRuleJS(tradeService, brokerModel, tradestrategy.getStrategyData(),
                tradestrategy.getId(), strategy.getClassName(), strategy.getRules().getFirst());
        assertNotNull(strategyProxy);
        strategyProxy.execute();

        while (!strategyProxy.isWaiting()) {

            Thread.sleep(250);
        }

        _log.info(" Test Initialized");
        tradestrategy.getStrategyData().populateCandleSeries(tradestrategy.getTradingday(), tradestrategy.getChartDays(), tradestrategy.getBarSize(), Side.BOT.equals(tradestrategy.getSide()), 250);
        strategyProxy.cancel();
        deleteAfter = false;
    }

    @Test
    @Order(200)
    public void posMgrFHXRBHYRStrategyJS() throws Exception {

        deleteAfter = true;
        tradestrategy = tradeService.findTradestrategyById(tradestrategy.getId());
        Strategy strategy = (Strategy) DAOStrategy.newInstance().getObject();
        assertTrue(strategy.hasStrategyManager());
        strategy = strategy.getStrategyManager();

        tradestrategy.setStrategy(strategy);
        tradestrategy = tradeService.saveAspect(tradestrategy);

        assertEquals(1, tradestrategy.getTradeOrders().size());
        TradeOrder tradeOrder = tradestrategy.getTradeOrders().getFirst();
        tradeOrder.setStatus(OrderStatus.SUBMITTED);
        tradeOrder = tradeService.saveAspect(tradeOrder);
        TradeOrderfill tradeOrderfill = new TradeOrderfill(tradeOrder, tradeOrder.getAccountNumber(), tradeOrder.getAuxPrice(),
                tradeOrder.getQuantity(), Exchange.SMART, String.valueOf(tradeOrder.getOrderKey()), tradeOrder.getAuxPrice(), tradeOrder.getQuantity(), (tradeOrder.getAction().equals(Action.BUY) ? Side.BOT : Side.SLD),
                tradeOrder.getOrderCreateDate());
        tradeOrder.addTradeOrderfill(tradeOrderfill);
        tradeService.saveTradeOrderfill(tradeOrder);

        strategy = tradeService.findStrategyById(tradestrategy.getStrategy().getId());
        String fileName = strategyDir + "/" + IStrategyRule.PACKAGE.replace('.', '/') + strategy.getClassName()
                + ".js";
        String content = TradestrategyBase.readFile(fileName);

        if (null != content && strategy.getRules().isEmpty()) {

            Rule nextRule = new Rule(strategy, true, 1, null,
                    content.getBytes(), ContentType.JAVASCRIPT);
            strategy.getRules().add(nextRule);
            strategy = this.tradeService.saveAspect(strategy);
        }

        tradestrategy.setStrategyData(StrategyData.create(tradestrategy));
        strategyProxy = new StrategyRuleJS(tradeService, brokerModel, tradestrategy.getStrategyData(),
                tradestrategy.getId(), strategy.getClassName(), strategy.getRules().getFirst());
        assertNotNull(strategyProxy);
        strategyProxy.execute();

        while (!strategyProxy.isWaiting()) {

            Thread.sleep(250);
        }

        _log.info(" Test Initialized");
        tradestrategy.getStrategyData().populateCandleSeries(tradestrategy.getTradingday(), tradestrategy.getChartDays(), tradestrategy.getBarSize(), Side.BOT.equals(tradestrategy.getSide()), 250);
        strategyProxy.cancel();
        TradePosition tradePosition = strategyProxy.getOpenTradePosition();
        assertNotNull(tradePosition);
        this.addRecord(tradePosition);
    }
}
