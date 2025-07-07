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
package org.trade.core;

import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.AccountType;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.DAOPortfolio;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.SECType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class TradestrategyBase {


    public TradestrategyBase() {
    }

    /**
     * Method getTestTradestrategy.
     *
     * @return Tradestrategy
     */
    public static Tradestrategy createTestTradestrategy(TradeService tradeService, String symbol) throws Exception {

        Tradestrategy tradestrategy;
        Strategy strategy = (Strategy) DAOStrategy.newInstance().getObject();
        Portfolio portfolio = (Portfolio) DAOPortfolio.newInstance().getObject();
        portfolio = tradeService.findPortfolioByName(portfolio.getName());

        if (portfolio.getAccounts().isEmpty()) {

            Account account = new Account(symbol, getRandomNumber(8), Currency.USD, AccountType.INDIVIDUAL);
            account.setAvailableFunds(new BigDecimal(25000));
            account.setBuyingPower(new BigDecimal(100000));
            account.setCashBalance(new BigDecimal(25000));
            portfolio.getAccounts().add(account);
            portfolio = tradeService.savePortfolio(portfolio);
        }

        ZonedDateTime open = TradingCalendar
                .getTradingDayStart(TradingCalendar.getPrevTradingDay(TradingCalendar.getDateTimeNowMarketTimeZone()));

        Contract contract = tradeService.findContractByUniqueKey(SECType.STOCK, symbol, Exchange.SMART, Currency.USD, null);

        if (null == contract) {

            contract = new Contract(SECType.STOCK, symbol, Exchange.SMART, Currency.USD, null, null);
            contract = tradeService.saveAspect(contract);

        } else {

            tradestrategy = tradeService.findTradestrategyByUniqueKeys(open, strategy.getName(),
                    contract.getId(), portfolio.getName());

            if (null != tradestrategy) {

                Tradestrategy instance = tradeService.findTradestrategyById(tradestrategy.getId());
                instance = tradeService.saveAspect(instance);
                Hashtable<Integer, TradePosition> tradePositions = new Hashtable<>();

                for (TradeOrder tradeOrder : instance.getTradeOrders()) {

                    if (tradeOrder.hasTradePosition()) {

                        tradePositions.put(tradeOrder.getTradePosition().getId(),
                                tradeOrder.getTradePosition());
                    }

                    if (null != tradeOrder.getId()) {

                        tradeService.deleteAspect(tradeOrder);
                    }
                }

                for (TradePosition tradePosition : tradePositions.values()) {

                    tradePosition = tradeService.findTradePositionById(tradePosition.getId());
                    /*
                     * Remove the open trade position from contract if this is a
                     * tradePosition to be deleted.
                     */
                    if (tradePosition.equals(instance.getContract().getTradePosition())) {

                        instance.getContract().setTradePosition(null);
                        instance.setContract(tradeService.saveAspect(instance.getContract()));
                    }
                    tradeService.deleteAspect(tradePosition);
                }

                instance.getTradeOrders().clear();
                return instance;
            }
        }

        Tradingday tradingday = Tradingday.newInstance(open);
        Tradingday instanceTradingDay = tradeService.findTradingdayByOpenCloseDate(tradingday.getOpen(), tradingday.getClose());

        if (null != instanceTradingDay) {

            tradingday.getTradestrategies().clear();
            tradingday = instanceTradingDay;
        }

        tradestrategy = new Tradestrategy(contract, tradingday, strategy, portfolio, new BigDecimal(100), "BUY", "0",
                true, ChartDays.TWO_DAYS, BarSize.FIVE_MIN);
        tradingday.addTradestrategy(tradestrategy);
        tradeService.saveTradingday(tradingday);
        Tradestrategy instance = tradeService.findTradestrategyById(tradestrategy.getId());
        instance.setStrategyData(StrategyData.create(instance));
        return instance;
    }

    /**
     * Method clearDBData.
     */
    public static void clearDBData(TradeService tradeService, Tradestrategy tradestrategy) throws Exception {


        if (null == tradestrategy || null == tradestrategy.getId()) {

            return;
        }

        tradestrategy = tradeService.findTradestrategyById(tradestrategy.getId());

        Portfolio portfolio = tradestrategy.getPortfolio();

        List<Account> accounts = portfolio.getAccounts();

        for (Account account : accounts) {

            tradeService.deleteAspect(account);
        }

        Tradingday tradingday = tradestrategy.getTradingday();
        tradeService.deleteAspect(tradingday);
        Contract contract = tradestrategy.getContract();
        contract.setTradePosition(null);
        contract = tradeService.saveAspect(contract);
        tradeService.deleteAspect(contract);

        portfolio = tradeService.findPortfolioById(portfolio.getId());

        if (!portfolio.getIsDefault() && portfolio.getTradestrategies().isEmpty()) {

            tradeService.deleteAspect(portfolio);
        }
    }

    public static String getRandomNumber(int length) {

        int mutiplier = 1;

        for (int i = 0; i < length; i++) {

            mutiplier = mutiplier * 10;
        }
        Random random = new Random();
        Integer number = random.nextInt((mutiplier - 1) + 1); // Generates a number between min (inclusive) and max (inclusive)
        return String.format("%0" + length + "d", number);
    }
}
