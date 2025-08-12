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
import org.trade.core.persistent.dao.ContractLite;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.Tradingdays;
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
import org.trade.core.valuetype.Side;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.fail;

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

       return createTestTradestrategy(tradeService,  symbol, Side.BOT, ChartDays.ONE_DAY, BarSize.FIVE_MIN);
    }
    /**
     * Method getTestTradestrategy.
     *
     * @return Tradestrategy
     */
    public static Tradestrategy createTestTradestrategy(TradeService tradeService, String symbol, String side, Integer chartDays, Integer barSize) throws Exception {

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
        } else {

            tradestrategy = tradeService.findTradestrategyByUniqueKeys(open, strategy.getName(),
                    contract, portfolio.getName());

            if (null != tradestrategy) {

                Tradestrategy instance = tradeService.findTradestrategyById(tradestrategy.getId());
                instance = tradeService.saveAspect(instance);
                Hashtable<Long, TradePosition> tradePositions = new Hashtable<>();

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
                    if (tradePosition.equals(instance.getContractLite().getTradePosition())) {

                        instance.getContractLite().setTradePosition(null);
                        instance.setContractLite(tradeService.saveAspect(instance.getContractLite()));
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

        tradestrategy = new Tradestrategy(contract, tradingday, strategy, portfolio, new BigDecimal(100), side, "0",
                true, chartDays, barSize);
        tradingday.addTradestrategy(tradestrategy);
        tradingday = tradeService.saveTradingday(tradingday);
        Tradestrategy instance = tradingday.getTradestrategies().getLast();
        instance = tradeService.findTradestrategyById(instance);
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

        Tradingdays tradingdays = tradeService.findTradingdaysByDateRange(tradestrategy.getTradingday().getOpen(), tradestrategy.getTradingday().getClose());

        for (Tradingday tradingday : tradingdays.getTradingdays()) {

            for (Tradestrategy tradestrategy0 : tradingday.getTradestrategies()) {

                ContractLite contractLite = tradestrategy0.getContractLite();
                Portfolio portfolio = tradestrategy0.getPortfolio();
                tradeService.deleteAspect(tradingday);

                if (null != contractLite.getTradePosition()) {

                    contractLite.setTradePosition(null);
                    contractLite = tradeService.saveAspect(contractLite);
                }

                tradeService.deleteAspect(contractLite);
                portfolio = tradeService.findPortfolioById(portfolio.getId());

                List<Account> accounts = portfolio.getAccounts();

                for (Account account : accounts) {

                    tradeService.deleteAspect(account);
                }

                if (!portfolio.getIsDefault() && portfolio.getTradestrategies().isEmpty()) {


                    tradeService.deleteAspect(portfolio);
                }
            }
        }
    }

    public static String getRandomNumber(int length) {

        int mutiplier = 1;

        for (int i = 0; i < length; i++) {

            mutiplier = mutiplier * 10;
        }

        Random random = new Random();

        // Generates a number between min (inclusive) and max (inclusive)
        Integer number = random.nextInt((mutiplier - 1) + 1);
        return String.format("%0" + length + "d", number);
    }

    /**
     * Method readFile.
     *
     * @param fileName String
     * @return String
     */
    public static String readFile(String fileName) {

        File file = new File(fileName);

        if (!file.exists()) {

            return null;
        }

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
    public static void writeFile(String fileName, String content) {

        try (OutputStream out = new FileOutputStream(fileName)) {

            out.write(content.getBytes());
        } catch (IOException ex) {

            fail("Failed to write OutputStream msg: " + ex.getMessage());
        }
    }
}
