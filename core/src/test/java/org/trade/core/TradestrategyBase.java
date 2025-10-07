package org.trade.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.trade.core.dao.Aspect;
import org.trade.core.persistent.TradeService;
import org.trade.core.persistent.account.Account;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.ContractLite;
import org.trade.core.persistent.portfolio.Portfolio;
import org.trade.core.persistent.rule.Rule;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.series.indicator.StrategyData;
import org.trade.core.persistent.tradingday.Tradingday;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TradestrategyBase {

    private final static Logger _log = LoggerFactory.getLogger(TradestrategyBase.class);
    private final static LinkedList<Aspect> aspects = new LinkedList<>();

    @Autowired
    public TradeService tradeService;

    public TradestrategyBase() {

    }

    /**
     * Method getTestTradestrategy.
     *
     * @return Tradestrategy
     */
    public Tradestrategy createTestTradestrategy(String symbol) throws Exception {

        return createTestTradestrategy(null, symbol, Side.BOT, ChartDays.ONE_DAY, BarSize.FIVE_MIN);
    }

    /**
     * Method getTestTradestrategy.
     *
     * @return Tradestrategy
     */
    public Tradestrategy createTestTradestrategy(Strategy strategy, String symbol, String side, Integer chartDays, Integer barSize) throws Exception {

        Tradestrategy tradestrategy;

        if (null == strategy) {
            strategy = (Strategy) DAOStrategy.newInstance().getObject();
        }

        Portfolio portfolio = (Portfolio) DAOPortfolio.newInstance().getObject();
        portfolio = tradeService.getPortfolioService().findByName(portfolio.getName());

        if (portfolio.getAccounts().isEmpty()) {

            Account account = new Account(symbol, getRandomNumber(8), Currency.USD, AccountType.INDIVIDUAL);
            account.setAvailableFunds(new BigDecimal(25000));
            account.setBuyingPower(new BigDecimal(100000));
            account.setCashBalance(new BigDecimal(25000));
            portfolio.getAccounts().add(account);
            portfolio = tradeService.savePortfolio(portfolio);
            this.addRecord(portfolio.getAccounts().getFirst());
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
                this.addRecord(instance);
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
                        ContractLite contractLite = tradeService.saveAspect(instance.getContractLite());
                        instance.setContractLite(contractLite);
                        this.addRecord(contractLite);
                    }
                    tradeService.deleteAspect(tradePosition);
                }

                instance.getTradeOrders().clear();
                return instance;
            }
        }

        Tradingday tradingday = Tradingday.newInstance(open);
        Tradingday instanceTradingDay = tradeService.getTradingdayService().findByOpenCloseDate(tradingday.getOpen(), tradingday.getClose());

        if (null != instanceTradingDay) {

            tradingday.getTradestrategies().clear();
            tradingday = instanceTradingDay;
        }

        tradestrategy = new Tradestrategy(contract, tradingday, strategy, portfolio, new BigDecimal(100), side, "0",
                true, chartDays, barSize);
        tradingday.addTradestrategy(tradestrategy);
        tradingday = tradeService.saveTradingday(tradingday);
        this.addRecord(tradingday.getTradestrategies().getLast().getContract());
        this.addRecord(tradingday);
        Tradestrategy instance = tradingday.getTradestrategies().getLast();
        instance = tradeService.findTradestrategyById(instance);
        instance.setStrategyData(StrategyData.create(instance));
        return instance;
    }

    public Long addTradeOrder(Tradestrategy tradestrategy, String action, String orderType, BigDecimal price, BigDecimal limitPrice, double stop) throws Exception {

        double risk = tradestrategy.getRiskAmount().doubleValue();
        int quantity = (int) ((int) risk / stop);

        TradeOrder tradeOrder = new TradeOrder(tradestrategy, action, orderType, quantity, price,
                limitPrice, TradingCalendar.getDateTimeNowMarketTimeZone());
        tradeOrder.setOrderKey((BigDecimal.valueOf(Math.random() * 1000000)).intValue());
        tradeOrder.setClientId(0);
        tradeOrder.setTransmit(true);
        tradeOrder.setStatus("SUBMITTED");
        tradeOrder.validate();
        tradeOrder = tradeService.saveTradeOrder(tradeOrder);

        assertNotNull(tradeOrder);
        _log.info("IdOrder: {}", tradeOrder.getId());
        return tradeOrder.getId();
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

    public void addRecord(Aspect entity) {

        aspects.add(entity);
    }

    public void deleteRecords() {

        try {

            Iterator<Aspect> descendingIterator = aspects.descendingIterator();

            while (descendingIterator.hasNext()) {

                Aspect aspect = descendingIterator.next();
                aspect = tradeService.findAspectById(aspect);
                tradeService.deleteAspect(aspect);
            }

            Iterable<Rule> rules = tradeService.getRuleService().findAll();
            rules.forEach(rule -> tradeService.deleteAspect(rule));
        } catch (ClassNotFoundException ex) {

            throw new RuntimeException(ex);
        }
    }
}
