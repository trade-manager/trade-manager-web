package org.trade.core.persistent.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Repository
public class TradestrategyRepositoryImpl implements TradestrategyRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method findTradestrategyLiteById.
     *
     * @param id Integer
     * @return TradestrategyLite
     */
    public TradestrategyLite findTradestrategyLiteById(Integer id) {

        TradestrategyLite instance = entityManager.find(TradestrategyLite.class, id);
        return instance;
    }

    /**
     * Method findVersionById.
     *
     * @param id Integer
     * @return Integer
     */
    public synchronized Integer findVersionById(Integer id) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TradestrategyLite> query = builder.createQuery(TradestrategyLite.class);
        Root<TradestrategyLite> from = query.from(TradestrategyLite.class);

        CriteriaQuery<TradestrategyLite> select = query.multiselect(from.get("id"),
                from.get("version"));
        Predicate predicate = builder.equal(from.get("id"), id);
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
     * @param idTradestrategy Integer
     * @return PositionOrders
     */
    public TradestrategyOrders findPositionOrdersByTradestrategyId(Integer idTradestrategy) {

        TradestrategyOrders instance = entityManager.find(TradestrategyOrders.class, idTradestrategy);
        /*
         * If we have an open position get all the orders for that position.
         * Note the position could have been opened by a different
         * tradestrategy. So this set of orders is for the position.
         */
        if (instance.hasOpenTradePosition()) {
            instance.getOpenTradePosition().getTradeOrders().size();
        }

        return instance;
    }

    /**
     * Method findByTradeId.
     *
     * @param idTradeOrder Integer
     * @return Tradestrategy
     */
    public synchronized Tradestrategy findByTradeOrderId(Integer idTradeOrder) {

        Tradestrategy tradestrategy = null;
        TradeOrder instance = entityManager.find(TradeOrder.class, idTradeOrder);
        if (null != instance) {
            tradestrategy = instance.getTradestrategy();
            tradestrategy.getContract();
        }

        return tradestrategy;
    }

    /**
     * Method findTradestrategyByUniqueKeys.
     *
     * @param open          Date
     * @param strategyName  String
     * @param idContract    Integer
     * @param portfolioName String
     * @return Tradestrategy
     */
    public Tradestrategy findTradestrategyByUniqueKeys(ZonedDateTime open, String strategyName, Integer idContract,
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

        if (null != idContract) {

            Join<Tradestrategy, Contract> contract = from.join("contract");
            Predicate predicate = builder.equal(contract.get("id"), idContract);
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

    /**
     * Method findTradestrategyDistinctByDateRange.
     *
     * @param fromOpen ZonedDateTime
     * @param toOpen   ZonedDateTime
     * @return Vector<ComboItem>
     */
    public List<Tradestrategy> findTradestrategyDistinctByDateRange(ZonedDateTime fromOpen, ZonedDateTime toOpen) {


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

    /**
     * Method findTradestrategyContractDistinctByDateRange.
     *
     * @param fromOpen ZonedDateTime
     * @param toOpen   ZonedDateTime
     * @return Vector<ComboItem>
     */
    public List<Tradestrategy> findTradestrategyContractDistinctByDateRange(ZonedDateTime fromOpen,
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
}