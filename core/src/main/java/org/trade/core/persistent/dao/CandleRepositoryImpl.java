package org.trade.core.persistent.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.trade.core.persistent.dao.series.indicator.CandleSeries;
import org.trade.core.persistent.dao.series.indicator.candle.CandleItem;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Repository
public class CandleRepositoryImpl implements CandleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method persistCandleSeries.
     *
     * @param candleSeries CandleSeries
     */
    public void persistCandleSeries(final CandleSeries candleSeries) throws Exception {

        Candle transientInstance;

        if (candleSeries.isEmpty()) {
            return;
        }

        entityManager.getTransaction().begin();
        Tradingday tradingday = null;
        Contract contract = findContractById(candleSeries.getContract().getId());


        for (int i = 0; i < candleSeries.getItemCount(); i++) {

            CandleItem candleItem = (CandleItem) candleSeries.getDataItem(i);

            if (null != candleItem.getCandle().getId()) {

                Candle instance = entityManager.find(Candle.class, candleItem.getCandle().getId());

                if (instance.equals(candleItem.getCandle())) {

                    continue;
                } else {

                    // This should never happen.
                    throw new Exception("Count: " + i + " Symbol: " + candleSeries.getSymbol() + "candleid: "
                            + candleItem.getCandle().getId() + " open: "
                            + candleItem.getCandle().getStartPeriod());
                }
            }

            if (!candleItem.getCandle().getTradingday().equals(tradingday)) {

                if (null == candleItem.getCandle().getTradingday().getId()) {

                    tradingday = findTradingdayByDate(candleItem.getCandle().getTradingday().getOpen(),
                            candleItem.getCandle().getTradingday().getClose());
                } else {

                    tradingday = findTradingdayById(candleItem.getCandle().getTradingday().getId());
                }

                if (null == tradingday) {

                    entityManager.persist(candleItem.getCandle().getTradingday());
                    entityManager.getTransaction().commit();
                    entityManager.getTransaction().begin();
                    tradingday = candleItem.getCandle().getTradingday();
                } else {

                    Integer barSize = candleSeries.getBarSize();
                    String hqlDelete = "delete Candle where contract = :contract and tradingday = :tradingday and barSize = :barSize";
                    entityManager.createQuery(hqlDelete).setParameter("contract", contract)
                            .setParameter("tradingday", tradingday).setParameter("barSize", barSize)
                            .executeUpdate();
                    entityManager.getTransaction().commit();
                    entityManager.getTransaction().begin();
                }
            }

            transientInstance = candleItem.getCandle();
            transientInstance.setTradingday(tradingday);
            transientInstance.setContract(contract);
            entityManager.persist(transientInstance);

            // Commit every 50 rows
            if ((Math.floor(i / 50d) == (i / 50d)) && (i > 0)) {
                entityManager.getTransaction().commit();
                entityManager.getTransaction().begin();
            }
        }
    }

    /**
     * Method findByContractAndDateRange.
     *
     * @param idContract  Integer
     * @param startPeriod Date
     * @param endPeriod   Date
     * @return List<Candle>
     */
    public List<Candle> findByContractAndDateRange(Integer idContract, ZonedDateTime startPeriod,
                                                   ZonedDateTime endPeriod, Integer barSize) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Candle> query = builder.createQuery(Candle.class);
        Root<Candle> from = query.from(Candle.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != idContract) {

            Join<Candle, Contract> contract = from.join("contract");
            Predicate predicate = builder.equal(contract.get("idContract"), idContract);
            predicates.add(predicate);
        }

        if (null != startPeriod) {

            Expression<ZonedDateTime> start = from.get("startPeriod");
            Predicate predicate = builder.greaterThanOrEqualTo(start, startPeriod);
            predicates.add(predicate);
        }

        if (null != endPeriod) {

            Expression<ZonedDateTime> end = from.get("endPeriod");
            Predicate predicate = builder.lessThanOrEqualTo(end, endPeriod);
            predicates.add(predicate);
        }

        if (null != barSize) {

            Predicate predicate = builder.equal(from.get("barSize"), barSize);
            predicates.add(predicate);
        }
        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Candle> typedQuery = entityManager.createQuery(query);
        List<Candle> items = typedQuery.getResultList();
        return items;
    }

    /**
     * Method findCandlesByContractDateRangeBarSize.
     *
     * @param idContract    Integer
     * @param startOpenDate ZonedDateTime
     * @param endOpenDate   ZonedDateTime
     * @param barSize       Integer
     * @return List<Candle>
     */
    public List<Candle> findCandlesByContractDateRangeBarSize(Integer idContract, ZonedDateTime startOpenDate,
                                                              ZonedDateTime endOpenDate, Integer barSize) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Candle> query = builder.createQuery(Candle.class);
        Root<Candle> from = query.from(Candle.class);
        query.select(from);
        query.orderBy(builder.asc(from.get("startPeriod")));
        List<Predicate> predicates = new ArrayList<>();

        if (null != idContract) {

            Join<Candle, Contract> contract = from.join("contract");
            Predicate predicateContract = builder.equal(contract.get("id"), idContract);
            predicates.add(predicateContract);
        }

        if (null != startOpenDate) {

            Join<Candle, Tradingday> tradingdayOpenDate = from.join("tradingday");
            Predicate predicateStartDate = builder
                    .greaterThanOrEqualTo(tradingdayOpenDate.get("open"), startOpenDate);
            predicates.add(predicateStartDate);
            Predicate predicateEndDate = builder
                    .lessThanOrEqualTo(tradingdayOpenDate.get("open"), endOpenDate);
            predicates.add(predicateEndDate);
        }

        if (null != barSize) {

            Predicate predicate = builder.equal(from.get("barSize"), barSize);
            predicates.add(predicate);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Candle> typedQuery = entityManager.createQuery(query);
        List<Candle> items = typedQuery.getResultList();
        return items;
    }

    /**
     * Method findByUniqueKey.
     *
     * @param idTradingday Integer
     * @param idContract   Integer
     * @param startPeriod  ZonedDateTime
     * @param endPeriod    ZonedDateTime
     * @return Candle
     */
    public Candle findByUniqueKey(Integer idTradingday, Integer idContract, ZonedDateTime startPeriod,
                                  ZonedDateTime endPeriod, Integer barSize) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Candle> query = builder.createQuery(Candle.class);
        Root<Candle> from = query.from(Candle.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != idTradingday) {

            Join<Candle, Tradingday> tradingday = from.join("tradingday");
            Predicate predicate = builder.equal(tradingday.get("id"), idTradingday);
            predicates.add(predicate);
        }

        if (null != idContract) {

            Join<Candle, Contract> contract = from.join("contract");
            Predicate predicate = builder.equal(contract.get("id"), idContract);
            predicates.add(predicate);
        }

        if (null != startPeriod) {

            Predicate predicate = builder.equal(from.get("startPeriod"), startPeriod);
            predicates.add(predicate);
        }
        if (null != endPeriod) {

            Predicate predicate = builder.equal(from.get("endPeriod"), endPeriod);
            predicates.add(predicate);
        }
        if (null != barSize) {

            Predicate predicate = builder.equal(from.get("barSize"), barSize);
            predicates.add(predicate);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Candle> typedQuery = entityManager.createQuery(query);
        List<Candle> items = typedQuery.getResultList();

        if (!items.isEmpty()) {
            return items.getFirst();
        }
        return null;
    }

    /**
     * Method findCandleCount.
     *
     * @param idTradingday Integer
     * @param idContract   Integer
     * @return Long
     */
    public Long findCandleCount(Integer idTradingday, Integer idContract) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = builder.createQuery();
        Root<Candle> from = query.from(Candle.class);
        Expression<Long> id = from.get("id");
        Expression<Long> minExpression = builder.count(id);

        List<Predicate> predicates = new ArrayList<>();

        if (null != idTradingday) {

            Join<Candle, Tradingday> tradingday = from.join("tradingday");
            Predicate predicate = builder.equal(tradingday.get("id"), idTradingday);
            predicates.add(predicate);
        }

        if (null != idContract) {

            Join<Candle, Contract> contract = from.join("contract");
            Predicate predicate = builder.equal(contract.get("id"), idContract);
            predicates.add(predicate);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        CriteriaQuery<Object> select = query.select(minExpression);
        TypedQuery<Object> typedQuery = entityManager.createQuery(select);
        Object item = typedQuery.getSingleResult();

        if (null == item) {
            item = 0L;
        }

        return (Long) item;
    }

    /**
     * Method findContractById.
     *
     * @param id Integer
     * @return Contract
     */
    private Contract findContractById(Integer id) {

        return entityManager.find(Contract.class, id);
    }

    /**
     * Method findContractById.
     *
     * @param id Integer
     * @return Contract
     */
    private Tradingday findTradingdayById(Integer id) {

        return entityManager.find(Tradingday.class, id);
    }

    /**
     * Method findTradingdayByDate.
     *
     * @param open  ZonedDateTime
     * @param close ZonedDateTime
     * @return Tradingday
     */
    private Tradingday findTradingdayByDate(ZonedDateTime open, ZonedDateTime close) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tradingday> query = builder.createQuery(Tradingday.class);
        Root<Tradingday> from = query.from(Tradingday.class);
        query.select(from);

        if (null != open) {

            query.where(builder.equal(from.get("open"), open));
        }

        if (null != close) {

            query.where(builder.equal(from.get("close"), close));
        }

        List<Tradingday> items = entityManager.createQuery(query).getResultList();

        if (!items.isEmpty()) {

            return items.getFirst();
        }
        return null;

    }
}