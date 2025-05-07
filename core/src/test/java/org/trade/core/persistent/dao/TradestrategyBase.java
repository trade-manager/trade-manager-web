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

import org.trade.core.dao.Aspect;
import org.trade.core.dao.AspectHome;
import org.trade.core.dao.Aspects;
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
import java.util.Objects;

/**
 *
 */
public class TradestrategyBase {

    /**
     * Method getTestTradestrategy.
     *
     * @return Tradestrategy
     */
    public static Tradestrategy getTestTradestrategy(String symbol) throws Exception {
        ContractHome contractHome = new ContractHome();
        PortfolioHome portfolioHome = new PortfolioHome();
        TradestrategyHome tradestrategyHome = new TradestrategyHome();
        AspectHome aspectHome = new AspectHome();

        Tradestrategy tradestrategy;
        Strategy strategy = (Strategy) DAOStrategy.newInstance().getObject();
        Portfolio portfolio = (Portfolio) Objects.requireNonNull(DAOPortfolio.newInstance()).getObject();
        portfolio = portfolioHome.findByName(portfolio.getName());
        if (portfolio.getPortfolioAccounts().isEmpty()) {
            Account account = new Account("Test", "T123456", Currency.USD, AccountType.INDIVIDUAL);
            account.setAvailableFunds(new BigDecimal(25000));
            account.setBuyingPower(new BigDecimal(100000));
            account.setCashBalance(new BigDecimal(25000));
            PortfolioAccount portfolioAccount = new PortfolioAccount(portfolio, account);
            portfolio.getPortfolioAccounts().add(portfolioAccount);
            portfolio = aspectHome.persist(portfolio);
        }
        ZonedDateTime open = TradingCalendar
                .getTradingDayStart(TradingCalendar.getPrevTradingDay(TradingCalendar.getDateTimeNowMarketTimeZone()));

        Contract contract = contractHome.findByUniqueKey(SECType.STOCK, symbol, Exchange.SMART, Currency.USD, null);
        if (null == contract) {
            contract = new Contract(SECType.STOCK, symbol, Exchange.SMART, Currency.USD, null, null);
            contract = aspectHome.persist(contract);

        } else {
            tradestrategy = tradestrategyHome.findTradestrategyByUniqueKeys(open, strategy.getName(),
                    contract.getId(), portfolio.getName());
            if (null != tradestrategy) {
                Tradestrategy transientInstance = tradestrategyHome.findById(tradestrategy.getId());
                transientInstance.setStatus(null);
                aspectHome.persist(transientInstance);

                Hashtable<Integer, TradePosition> tradePositions = new Hashtable<>();
                for (TradeOrder tradeOrder : transientInstance.getTradeOrders()) {
                    if (tradeOrder.hasTradePosition())
                        tradePositions.put(tradeOrder.getTradePosition().getId(),
                                tradeOrder.getTradePosition());

                    if (null != tradeOrder.getId()) {
                        aspectHome.remove(tradeOrder);
                    }
                }

                for (TradePosition tradePosition : tradePositions.values()) {
                    tradePosition = (TradePosition) aspectHome.findById(tradePosition);
                    /*
                     * Remove the open trade position from contract if this is a
                     * tradePosition to be deleted.
                     */
                    if (tradePosition.equals(transientInstance.getContract().getTradePosition())) {
                        transientInstance.getContract().setTradePosition(null);
                        aspectHome.persist(transientInstance.getContract());
                    }
                    aspectHome.remove(tradePosition);
                }

                transientInstance.getTradeOrders().clear();
                return transientInstance;
            }
        }
        TradingdayHome tradingdayHome = new TradingdayHome();
        Tradingday tradingday = Tradingday.newInstance(open);
        Tradingday instanceTradingDay = tradingdayHome.findByOpenCloseDate(tradingday.getOpen(), tradingday.getClose());
        if (null != instanceTradingDay) {
            tradingday.getTradestrategies().clear();
            tradingday = instanceTradingDay;
        }
        tradestrategy = new Tradestrategy(contract, tradingday, strategy, portfolio, new BigDecimal(100), "BUY", "0",
                true, ChartDays.TWO_DAYS, BarSize.FIVE_MIN);
        tradingday.addTradestrategy(tradestrategy);
        tradingdayHome.persist(tradingday);
        Tradestrategy instance = tradestrategyHome.findById(tradestrategy.getId());
        instance.setStrategyData(StrategyData.create(instance));
        return instance;
    }

    /**
     * Method clearDBData.
     */
    public static void clearDBData() throws Exception {

        AspectHome aspectHome = new AspectHome();
        Aspects contracts = aspectHome.findByClassName(Contract.class.getName());
        for (Aspect aspect : contracts.getAspect()) {
            ((Contract) aspect).setTradePosition(null);
            aspectHome.persist(aspect);
        }

        Aspects tradeOrders = aspectHome.findByClassName(TradeOrder.class.getName());
        for (Aspect aspect : tradeOrders.getAspect()) {
            aspectHome.remove(aspect);
        }

        Aspects tradePositions = aspectHome.findByClassName(TradePosition.class.getName());
        for (Aspect aspect : tradePositions.getAspect()) {
            aspectHome.remove(aspect);
        }
        Aspects portfolioAccounts = aspectHome.findByClassName(PortfolioAccount.class.getName());
        for (Aspect aspect : portfolioAccounts.getAspect()) {
            aspectHome.remove(aspect);
        }
        Aspects accounts = aspectHome.findByClassName(Account.class.getName());
        for (Aspect aspect : accounts.getAspect()) {
            aspectHome.remove(aspect);
        }
        Aspects tradestrategies = aspectHome.findByClassName(Tradestrategy.class.getName());
        for (Aspect aspect : tradestrategies.getAspect()) {
            aspectHome.remove(aspect);
        }
        contracts = aspectHome.findByClassName(Contract.class.getName());
        for (Aspect aspect : contracts.getAspect()) {
            aspectHome.remove(aspect);
        }
        Aspects tradingdays = aspectHome.findByClassName(Tradingday.class.getName());
        for (Aspect aspect : tradingdays.getAspect()) {
            aspectHome.remove(aspect);
        }
    }


}
