package org.trade.core.persistent.tradestrategy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trade.core.persistent.contract.Contract;
import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.portfolio.Portfolio;
import org.trade.core.persistent.strategy.Strategy;
import org.trade.core.persistent.tradingday.Tradingday;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class TradestrategyServiceImpl implements TradestrategyService {

    @PersistenceContext
    private EntityManager entityManager;

    private final TradestrategyRepository tradestrategyRepository;

    public TradestrategyServiceImpl(final TradestrategyRepository tradestrategyRepository) {

        this.tradestrategyRepository = tradestrategyRepository;
    }

    @Transactional
    public Tradestrategy findById(final Long id) {

        Tradestrategy tradestrategy = this.tradestrategyRepository.findById(id).orElse(null);

        if (null != tradestrategy) {

            for (TradeOrder tradeOrder : tradestrategy.getTradeOrders()) {

                tradeOrder.getTradeOrderfills().size();
            }

            return tradestrategy;
        }

        return null;
    }

    public List<Tradestrategy> findAll() {

        return tradestrategyRepository.findAll();
    }

    public Tradestrategy findByRequestId(Integer requestId) {

        return tradestrategyRepository.findByRequestId(requestId);
    }

    /**
     * Method findTradestrategyLiteById.
     *
     * @param tradestrategy Tradestrategy
     * @return TradestrategyLite
     */
    public TradestrategyLite findByTradestrategy(Tradestrategy tradestrategy) {

        TradestrategyLite instance = entityManager.find(TradestrategyLite.class, tradestrategy.getId());
        return instance;
    }

    /**
     * Method findVersionById.
     *
     * @param tradestrategyId Long
     * @return Integer
     */
    public Integer findVersionById(Long tradestrategyId) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TradestrategyLite> query = builder.createQuery(TradestrategyLite.class);
        Root<TradestrategyLite> from = query.from(TradestrategyLite.class);

        CriteriaQuery<TradestrategyLite> select = query.multiselect(from.get("id"),
                from.get("version"));
        Predicate predicate = builder.equal(from.get("id"), tradestrategyId);
        query.where(predicate);
        TypedQuery<TradestrategyLite> typedQuery = entityManager.createQuery(select);
        List<TradestrategyLite> items = typedQuery.getResultList();

        if (!items.isEmpty()) {

            return items.getFirst().getVersion();
        }

        return null;
    }

    /**
     * Method findPositionOrdersByTradestrategyId.
     *
     * @param tradestrategyId Integer
     * @return PositionOrders
     */
    public TradestrategyOrders findPositionOrdersById(Long tradestrategyId) {

        return entityManager.find(TradestrategyOrders.class, tradestrategyId);
    }

    public Tradestrategy findByTradeOrder(TradeOrder tradeOrder) {

        Tradestrategy tradestrategy = null;
        TradeOrder instance = entityManager.find(TradeOrder.class, tradeOrder.getId());

        if (null != instance) {

            tradestrategy = instance.getTradestrategy();
            tradestrategy.getContract();
        }

        return tradestrategy;
    }

    public Tradestrategy findByUniqueKeys(ZonedDateTime open, String strategyName, Contract contract,
                                          String portfolioName) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tradestrategy> query = builder.createQuery(Tradestrategy.class);
        Root<Tradestrategy> from = query.from(Tradestrategy.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != strategyName) {

            Join<Tradestrategy, Strategy> strategies = from.join("strategy");
            Predicate predicate = builder.equal(strategies.get("name"), strategyName);
            predicates.add(predicate);
        }

        if (null != portfolioName) {

            Join<Tradestrategy, Portfolio> portfolio = from.join("portfolio");
            Predicate predicate = builder.equal(portfolio.get("name"), portfolioName);
            predicates.add(predicate);
        }

        if (null != open) {

            Join<Tradestrategy, Tradingday> tradingday = from.join("tradingday");
            Predicate predicate = builder.equal(tradingday.get("open"), open);
            predicates.add(predicate);
        }

        if (null != contract) {

            Join<Tradestrategy, Contract> contractJoin = from.join("contract");
            Predicate predicate = builder.equal(contractJoin.get("id"), contract.getId());
            predicates.add(predicate);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Tradestrategy> typedQuery = entityManager.createQuery(query);
        List<Tradestrategy> items = typedQuery.getResultList();

        if (!items.isEmpty()) {

            return items.getFirst();
        }
        return null;

    }

    public List<Tradestrategy> findByDateRangeDistinctBarsizeAndChartDaysAndStrategy(ZonedDateTime fromOpen, ZonedDateTime toOpen) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tradestrategy> query = builder.createQuery(Tradestrategy.class);
        Root<Tradestrategy> from = query.from(Tradestrategy.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != fromOpen) {

            Join<Tradestrategy, Tradingday> tradingday = from.join("tradingday");
            Predicate predicate = builder.greaterThanOrEqualTo(tradingday.get("open"),
                    fromOpen);
            predicates.add(predicate);
        }

        if (null != toOpen) {

            Join<Tradestrategy, Tradingday> tradingday = from.join("tradingday");
            Predicate predicate = builder.lessThanOrEqualTo(tradingday.get("open"), toOpen);
            predicates.add(predicate);
        }

        query.multiselect(from.get("barSize"), from.get("chartDays"), from.join("strategy")).distinct(true);
        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Tradestrategy> typedQuery = entityManager.createQuery(query);
        List<Tradestrategy> items = typedQuery.getResultList();
        return items;

    }

    public List<Tradestrategy> findByDateRangeDistinctContract(ZonedDateTime fromOpen,
                                                               ZonedDateTime toOpen) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tradestrategy> query = builder.createQuery(Tradestrategy.class);
        Root<Tradestrategy> from = query.from(Tradestrategy.class);
        query.select(from);
        query.orderBy(builder.asc(from.join("contract").get("symbol")));

        List<Predicate> predicates = new ArrayList<>();

        if (null != fromOpen) {

            Join<Tradestrategy, Tradingday> tradingday = from.join("tradingday");
            Predicate predicate = builder.greaterThanOrEqualTo(tradingday.get("open"),
                    fromOpen);
            predicates.add(predicate);
        }

        if (null != toOpen) {

            Join<Tradestrategy, Tradingday> tradingday = from.join("tradingday");
            Predicate predicate = builder.lessThanOrEqualTo(tradingday.get("open"), toOpen);
            predicates.add(predicate);
        }

        query.multiselect(from.join("contract")).distinct(true);
        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Tradestrategy> typedQuery = entityManager.createQuery(query);
        List<Tradestrategy> items = typedQuery.getResultList();
        return items;
    }

    /**
     * Method findTradestrategyByDate.
     *
     * @param tradingday Tradingday
     * @return List<Tradestrategy>
     */
    public List<Tradestrategy> findByTradingday(Tradingday tradingday) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tradestrategy> query = builder.createQuery(Tradestrategy.class);
        Root<Tradestrategy> from = query.from(Tradestrategy.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != tradingday) {

            Join<Tradestrategy, Tradingday> tradingdayJoin = from.join("tradingday");
            Predicate predicate = builder.equal(tradingdayJoin.get("id"), tradingday.getId());
            predicates.add(predicate);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Tradestrategy> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }
}