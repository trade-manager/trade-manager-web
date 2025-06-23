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
package org.trade.core.persistent.dao;

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
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.DAOPortfolio;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Some tests for the DataUtilities class.
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootTest
@ContextConfiguration(classes = ApplicationRepositoryConfig.class,
        initializers = ApplicationProfileInitializer.class)
public class TradelogReportIT {

    private final static Logger _log = LoggerFactory.getLogger(TradelogReportIT.class);

    @Autowired
    private TradelogDetailRepository tradelogDetailRepository;

    @Autowired
    private TradelogSummaryRepository tradelogSummaryRepository;

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
    }

    /**
     * Method tearDown.
     */
    @AfterEach
    public void tearDown() {
    }

    /**
     * Method tearDownAfterClass.
     */
    @AfterAll
    public static void tearDownAfterClass() {
    }

    @Test
    public void tradelogDetails() throws IOException {

        Portfolio portfolio = (Portfolio) Objects.requireNonNull(DAOPortfolio.newInstance()).getObject();
        List<TradelogDetail> tradelogDetail = tradelogDetailRepository.findByTradelogDetail(portfolio, TradingCalendar.getYearStart(),
                TradingCalendar.getTradingDayEnd(TradingCalendar.getDateTimeNowMarketTimeZone()), false, null, new BigDecimal(0));
        assertTrue(tradelogDetail.isEmpty());

        for (TradelogDetail item : tradelogDetail) {

            _log.info("tradelogDetails tradelogDetail:  getOpen:{} getAction:{} getMarketBias:{} getName:{} getSymbol:{} getQuantity:{} getLongShort:{} getAverageFilledPrice:{} getFilledDate:{}", item.getOpen(), item.getAction(), item.getMarketBias(), item.getName(), item.getSymbol(), item.getQuantity(), item.getLongShort(), item.getAverageFilledPrice(), item.getFilledDate());
        }
    }

    @Test
    public void tradelogSummary() throws IOException {

        Portfolio portfolio = (Portfolio) Objects.requireNonNull(DAOPortfolio.newInstance()).getObject();
        List<TradelogSummary> tradelogSummary = tradelogSummaryRepository.findByTradelogSummary(portfolio,
                TradingCalendar.getYearStart(),
                TradingCalendar.getTradingDayEnd(TradingCalendar.getDateTimeNowMarketTimeZone()), null,
                new BigDecimal(0));
        assertTrue(tradelogSummary.isEmpty());

        for (TradelogSummary item : tradelogSummary) {

            _log.info("tradelogSummary tradelogDetail: getPeriod:{}getBattingAverage:{}getSimpleSharpeRatio:{}getQuantity:{}getGrossProfitLoss:{}getQuantity:{}getNetProfitLoss:{}", item.getPeriod(), item.getBattingAverage(), item.getSimpleSharpeRatio(), item.getQuantity(), item.getGrossProfitLoss(), item.getQuantity(), item.getNetProfitLoss());
        }
    }
}
