/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.core.strategy;

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
import org.trade.core.ApplicationProfileInitializer;
import org.trade.core.ApplicationRepositoryConfig;
import org.trade.core.TradestrategyBase;
import org.trade.core.broker.IBrokerModel;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.dao.strategy.StrategyRuleJS;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.Side;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class AbstractStrategyJSIT {

    private final static Logger _log = LoggerFactory.getLogger(AbstractStrategyJSIT.class);

    @Autowired
    private TradeService tradeService;

    private static final String symbol = "TEST-" + TradestrategyBase.getRandomNumber(4);
    private static Tradestrategy tradestrategy;
    private static IBrokerModel brokerModel;
    private static String templateName;
    private static String strategyDir;
    private static StrategyRuleJS strategyProxy;

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

        tradestrategy = TradestrategyBase.createTestTradestrategy(tradeService, symbol);
        assertNotNull(tradestrategy);
        StrategyData.doDummyData(tradestrategy.getStrategyData().getBaseCandleSeries(), tradestrategy.getTradingday(), 1, BarSize.FIVE_MIN, Side.BOT.equals(tradestrategy.getSide()), 0);

        strategyProxy = new StrategyRuleJS(tradeService, brokerModel, tradestrategy.getStrategyData(),
                tradestrategy.getId());
        assertNotNull(strategyProxy);
        strategyProxy.execute();

        do {
            Thread.sleep(1000);
        } while (!strategyProxy.isWaiting());
        _log.info(" Test Initialized");
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() throws Exception {

        brokerModel.onDisconnect();
        strategyProxy.cancel();
        TradestrategyBase.clearDBData(tradeService, tradestrategy);
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void runJavaScript() {

    }
}
