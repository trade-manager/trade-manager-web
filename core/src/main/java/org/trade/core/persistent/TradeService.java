package org.trade.core.persistent;

import org.trade.core.dao.Aspect;
import org.trade.core.dao.AspectService;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.dao.Candle;
import org.trade.core.persistent.dao.CodeType;
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.ContractLite;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.Rule;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.TradeOrderfill;
import org.trade.core.persistent.dao.TradePosition;
import org.trade.core.persistent.dao.TradelogReport;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.TradestrategyLite;
import org.trade.core.persistent.dao.TradestrategyOrders;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.tradingday.Tradingday;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;


/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface TradeService extends AspectService {


    public static final String PERSISTENT_PACKAGE = "org.trade.core.persistent.dao.";

    /**
     * @param entities
     */
    void deleteAllAspects(Iterable<? extends Aspect> entities);

    /**
     * @param symbol
     * @return
     */
    Optional<Contract> findContractBySymbol(String symbol);

    /**
     * Method findAllContracts.
     *
     * @return Iterable<Contract>
     */
    Iterable<Contract> findAllContracts();

    /**
     * Method findAllRules.
     *
     * @return Iterable<Rule>
     */
    Iterable<Rule> findAllRules();

    /**
     * Method findAllAccounts.
     *
     * @return Iterable<Account>
     */
    Iterable<Account> findAllAccounts();

    /**
     * Method saveTrading.
     *
     * @param instance Tradingday
     */
    Tradingday saveTradingday(Tradingday instance);

    /**
     * Method savePortfolio.
     *
     * @param instance Portfolio
     * @return Portfolio
     */
    Portfolio savePortfolio(Portfolio instance);


    /**
     * Method saveTradeOrder.
     *
     * @param instance TradeOrder
     * @return TradeOrder
     */
    TradeOrder saveTradeOrder(TradeOrder instance);

    /**
     * Method saveTradeOrderfill.
     *
     * @param tradeOrder TradeOrder
     * @return TradeOrder
     */
    TradeOrder saveTradeOrderfill(TradeOrder tradeOrder);

    /**
     * Method saveCandleSeries.
     *
     * @param candleSeries CandleSeries
     */
    void saveCandleSeries(CandleSeries candleSeries);

    /**
     * Method findAccountById.
     *
     * @param id Long
     * @return Account
     */
    Account findAccountById(Long id);

    /**
     * Method findAccountByNumber.
     *
     * @param accountNumber String
     * @return Account
     */
    Account findAccountByAccountNumber(String accountNumber);

    /**
     * Method findContractById.
     *
     * @param id Long
     * @return Contract
     */
    Contract findContractById(Long id);

    /**
     * Method findContractLiteById.
     *
     * @param id Long
     * @return ContractLite
     */
    ContractLite findContractLiteById(final Long id);

    /**
     * Method findTradeOrderById.
     *
     * @param id Long
     * @return TradeOrder
     */
    TradeOrder findTradeOrderById(Long id);

    /**
     * Method findContractByUniqueKey.
     *
     * @param SECType  String
     * @param symbol   String
     * @param exchange String
     * @param currency String
     * @return Contract
     */
    Contract findContractByUniqueKey(String SECType, String symbol, String exchange, String currency,
                                     ZonedDateTime expiry);

    /**
     * Method findTradestrategyById.
     *
     * @param tradestrategy Tradestrategy
     * @return Tradestrategy
     */
    Tradestrategy findTradestrategyById(Tradestrategy tradestrategy);

    /**
     * Method findTradestrategyById.
     *
     * @param id Long
     * @return Tradestrategy
     */
    Tradestrategy findTradestrategyById(Long id);

    /**
     * @param tradestrategy
     * @return
     */
    TradestrategyLite findTradestrategyLiteByTradestrategy(final Tradestrategy tradestrategy);

    /**
     * Method existTradestrategyById.
     *
     * @param requestId Integer
     * @return boolean
     */
    boolean existTradestrategyByRequestId(final Integer requestId);

    /**
     * Method findPositionOrdersByTradestrategyId.
     *
     * @param tradestrategyId Long
     * @return PositionOrders
     */
    TradestrategyOrders findPositionOrdersByTradestrategyId(Long tradestrategyId);

    /**
     * Method refreshPositionOrdersByTradestrategyId.
     *
     * @param positionOrders PositionOrders
     * @return PositionOrders
     */
    TradestrategyOrders refreshPositionOrdersByTradestrategyId(TradestrategyOrders positionOrders);

    /**
     * Method findTradestrategyByUniqueKeys.
     *
     * @param open          ZonedDateTime
     * @param strategy      String
     * @param contract      Contract
     * @param portfolioName String
     * @return Tradestrategy
     */
    Tradestrategy findTradestrategyByUniqueKeys(ZonedDateTime open, String strategy, Contract contract,
                                                String portfolioName);

    /**
     * @param requestId Integer
     * @return
     */
    Tradestrategy findTradestrategyByRequestId(Integer requestId);

    /**
     * Method findTradestrategyDistinctByDateRange.
     *
     * @param fromOpen ZonedDateTime
     * @param toOpen   ZonedDateTime
     * @return List<Tradestrategy>
     */
    List<Tradestrategy> findTradestrategyDistinctByDateRange(ZonedDateTime fromOpen, ZonedDateTime toOpen);

    /**
     * Method findTradestrategyContractDistinctByDateRange.
     *
     * @param fromOpen Date
     * @param toOpen   Date
     * @return List<Tradestrategy>
     */
    List<Tradestrategy> findTradestrategyContractDistinctByDateRange(ZonedDateTime fromOpen, ZonedDateTime toOpen);

    /**
     * Method findAllTradestrategies.
     *
     * @return List<Tradestrategy>
     */
    List<Tradestrategy> findAllTradestrategies();

    /**
     * Method findTradePositionById.
     *
     * @param tradePositionId Long
     * @return TradePosition
     */
    TradePosition findTradePositionById(Long tradePositionId);

    /**
     * Method findPortfolioById.
     *
     * @param id Long
     * @return Portfolio
     */
    Portfolio findPortfolioById(Long id);

    /**
     * Method findPortfolioByName.
     *
     * @param name String
     * @return Portfolio
     */

    Portfolio findPortfolioByName(String name);

    /**
     * Method findPortfolioDefault.
     *
     * @return Portfolio
     */
    Portfolio findPortfolioDefault();

    /**
     * Method resetDefaultPortfolio.
     *
     * @param instance Portfolio
     */
    void resetDefaultPortfolio(Portfolio instance);

    /**
     * Method removeTradingdayTradeOrders.
     *
     * @param instance Tradingday
     */
    void deleteTradingdayTradeOrders(Tradingday instance);

    /**
     * Method removeTradestrategyTradeOrders.
     *
     * @param instance Tradestrategy
     */
    void deleteTradestrategyTradeOrders(Tradestrategy instance);

    /**
     * Method findTradeOrderByKey.
     *
     * @param orderKey Integer
     * @return TradeOrder
     */
    TradeOrder findTradeOrderByKey(Integer orderKey);

    /**
     * Method findTradeOrderfillByExecId.
     *
     * @param execId String
     * @return TradeOrderfill
     */
    TradeOrderfill findTradeOrderfillByExecId(String execId);

    /**
     * Method findTradeOrderByMaxKey.
     *
     * @return Integer
     */
    Integer findTradeOrderByMaxKey();

    /**
     * Method findTradelogReport.
     *
     * @param portfolio     Portfolio
     * @param start         ZonedDateTime
     * @param end           ZonedDateTime
     * @param filter        boolean
     * @param symbol        String
     * @param winLossAmount BigDecimal
     * @return TradelogReport
     */
    TradelogReport findTradelogReport(Portfolio portfolio, ZonedDateTime start, ZonedDateTime end, boolean filter,
                                      String symbol, BigDecimal winLossAmount) throws IOException;

    /**
     * @param contract
     * @param startDate
     * @param endDate
     * @return
     */
    List<Candle> findCandlesByContractDateRangeBarSize(final Contract contract, final ZonedDateTime startDate,
                                                       final ZonedDateTime endDate, final Integer barSize);

    /**
     * @param contract
     * @param barSize
     * @return
     */
    List<Candle> findCandlesByContractAndBarSize(Contract contract, Integer barSize);

    /**
     * Method findCandleCount.
     *
     * @param contract Contract
     * @return Long
     */
    Long findCandleCount(final Contract contract);


    /**
     * Method findRuleById.
     *
     * @param ruleId Long
     * @return Rule
     */
    Rule findRuleById(Long ruleId);

    /**
     * Method findRulesAll.
     *
     * @return List<Rule>
     */
    List<Rule> findRulesAll();

    /**
     * Method findRulesByStrategy.
     *
     * @param strategy Strategy
     * @return List<Rule>
     */
    List<Rule> findRulesByStrategy(Strategy strategy);

    /**
     * Method findRulesByStrategyAndActive.
     *
     * @param strategy
     * @param active
     * @return
     */
    List<Rule> findRulesByStrategyAndActive(Strategy strategy, Boolean active);

    /**
     * Method findRuleByMaxVersion.
     *
     * @param strategy    Strategy
     * @param contentType String
     * @return Rule
     */
    Rule findRuleByMaxVersion(Strategy strategy, String contentType);

    /**
     * Method findStrategyById.
     *
     * @param id Long
     * @return Strategy
     */
    Strategy findStrategyById(Long id);

    /**
     * Method findStrategyByName.
     *
     * @param name String
     * @return Strategy
     */
    Strategy findStrategyByName(String name);

    /**
     * Method findStrategies.
     *
     * @return List<Strategy>
     */
    List<Strategy> findStrategies();

    /**
     * Method findAspectById.
     *
     * @param instance Aspect
     * @return Aspect
     */
    Aspect findAspectById(final Aspect instance) throws ClassNotFoundException;

    /**
     * Method saveAspect.
     *
     * @param instance Aspect
     * @return Aspect
     */
    <T extends Aspect> T saveAspect(T instance);

    /**
     * Delete all aspects.
     *
     * @param entities
     * @param <S>
     * @return
     */
    <S extends Aspect> List<S> saveAllAspects(final Iterable<S> entities);

    /**
     * Method saveAspect.
     *
     * @param instance        Aspect
     * @param overrideVersion boolean
     * @return Aspect
     */
    <T extends Aspect> T saveAspect(T instance, boolean overrideVersion);

    /**
     * Method removeAspect.
     *
     * @param instance Aspect
     */
    void deleteAspect(Aspect instance);

    /**
     * Method reassignStrategy.
     *
     * @param fromStrategy Strategy
     * @param toStrategy   Strategy
     * @param tradingday   Tradingday
     */
    void reassignStrategy(Strategy fromStrategy, Strategy toStrategy, Tradingday tradingday);

    /**
     * Method findCodeTypeByNameType.
     *
     * @param name String
     * @param type String
     * @return CodeType
     */
    CodeType findCodeTypeByNameType(String name, String type);
}
