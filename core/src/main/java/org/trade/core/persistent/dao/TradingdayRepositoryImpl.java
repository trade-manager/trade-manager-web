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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Repository
public class TradingdayRepositoryImpl implements TradingdayRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method findTradingdaysByDateRange.
     *
     * @param startDate ZonedDateTime
     * @param endDate   ZonedDateTime
     * @return Tradingdays
     */
    public Tradingdays findTradingdaysByDateRange(ZonedDateTime startDate, ZonedDateTime endDate) {


        Tradingdays tradingdays = new Tradingdays();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tradingday> query = builder.createQuery(Tradingday.class);
        Root<Tradingday> from = query.from(Tradingday.class);
        query.select(from);
        query.orderBy(builder.desc(from.get("open")));
        List<Predicate> predicates = new ArrayList<>();

        if (null != startDate) {

            Predicate predicate = builder.greaterThanOrEqualTo(from.get("open"), startDate);
            predicates.add(predicate);
        }
        if (null != endDate) {

            Predicate predicate = builder.lessThanOrEqualTo(from.get("open"), endDate);
            predicates.add(predicate);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Tradingday> typedQuery = entityManager.createQuery(query);
        List<Tradingday> items = typedQuery.getResultList();

        for (Tradingday tradingday : items) {

            tradingdays.add(tradingday);

            for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {

                tradestrategy.getTradeOrders().size();
                tradestrategy.getPortfolio().getPortfolioAccounts().size();
                tradestrategy.getStrategy().getIndicatorSeries().size();
            }
        }

        return tradingdays;

    }

    /**
     * Method findByOpen.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return Tradingday
     */
    public Tradingday findByOpenCloseDate(ZonedDateTime openDate, ZonedDateTime closeDate) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tradingday> query = builder.createQuery(Tradingday.class);
        Root<Tradingday> from = query.from(Tradingday.class);
        query.select(from);

        if (null != openDate) {
            query.where(builder.equal(from.get("open"), openDate));
        }

        if (null != closeDate) {
            query.where(builder.equal(from.get("close"), closeDate));
        }

        List<Tradingday> items = entityManager.createQuery(query).getResultList();

        for (Tradingday tradingday : items) {

            for (Tradestrategy tradestrategy : tradingday.getTradestrategies()) {

                tradestrategy.getTradeOrders().size();
                tradestrategy.getStrategy().getIndicatorSeries().size();
            }
        }

        if (!items.isEmpty()) {
            return items.getFirst();
        }
        return null;
    }

    /**
     * Method findStrategyByName.
     *
     * @param name String
     * @return Strategy
     */
    private Strategy findStrategyByName(String name) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Strategy> query = builder.createQuery(Strategy.class);
        Root<Strategy> from = query.from(Strategy.class);
        query.select(from);
        query.where(builder.equal(from.get("name"), name));
        List<Strategy> items = entityManager.createQuery(query).getResultList();

        if (!items.isEmpty()) {

            for (Strategy itme : items) {

                itme.getIndicatorSeries().size();
            }
            return items.getFirst();
        }
        return null;

    }

    /**
     * Method findTradingdayByOpenDate.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return Tradingday
     */
    private Tradingday findTradingdayByOpenCloseDate(ZonedDateTime openDate, ZonedDateTime closeDate) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tradingday> query = builder.createQuery(Tradingday.class);
        Root<Tradingday> from = query.from(Tradingday.class);
        query.select(from);

        if (null != openDate) {
            query.where(builder.equal(from.get("open"), openDate));
        }

        if (null != closeDate) {
            query.where(builder.equal(from.get("close"), closeDate));
        }

        List<Tradingday> items = entityManager.createQuery(query).getResultList();

        if (!items.isEmpty()) {

            return items.getFirst();
        }
        return null;
    }

    /**
     * Method findTradestrategyByDate.
     *
     * @param idTradingday Integer
     * @return List<Tradestrategy>
     */
    public List<Tradestrategy> findTradestrategyByTradingdayId(Integer idTradingday) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tradestrategy> query = builder.createQuery(Tradestrategy.class);
        Root<Tradestrategy> from = query.from(Tradestrategy.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != idTradingday) {

            Join<Tradestrategy, Tradingday> tradingday = from.join("tradingday");
            Predicate predicate = builder.equal(tradingday.get("id"), idTradingday);
            predicates.add(predicate);
        }
        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Tradestrategy> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    /**
     * Method findContractByUniqueKey.
     *
     * @param SECType    String
     * @param symbol     String
     * @param exchange   String
     * @param currency   String
     * @param expiryDate Date
     * @return Contract
     */
    private Contract findContractByUniqueKey(String SECType, String symbol, String exchange, String currency,
                                             ZonedDateTime expiryDate) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Contract> query = builder.createQuery(Contract.class);
        Root<Contract> from = query.from(Contract.class);
        query.select(from);
        List<Predicate> predicates = new ArrayList<>();

        if (null != SECType) {

            Predicate predicate = builder.equal(from.get("secType"), SECType);
            predicates.add(predicate);
        }
        if (null != symbol) {

            Predicate predicate = builder.equal(from.get("symbol"), symbol);
            predicates.add(predicate);
        }
        if (null != exchange) {

            Predicate predicate = builder.equal(from.get("exchange"), exchange);
            predicates.add(predicate);
        }
        if (null != currency) {

            Predicate predicate = builder.equal(from.get("currency"), currency);
            predicates.add(predicate);
        }
        if (null != expiryDate) {

            Integer yearExpiry = expiryDate.getYear();
            Expression<Integer> year = builder.function("year", Integer.class, from.get("expiry"));
            Predicate predicateYear = builder.equal(year, yearExpiry);
            predicates.add(predicateYear);

            int monthExpiry = expiryDate.getMonthValue();
            Expression<Integer> month = builder.function("month", Integer.class, from.get("expiry"));
            Predicate predicateMonth = builder.equal(month, 1 + monthExpiry);
            predicates.add(predicateMonth);
        }

        query.where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Contract> typedQuery = entityManager.createQuery(query);
        List<Contract> items = typedQuery.getResultList();

        if (!items.isEmpty()) {

            return items.getFirst();
        }
        return null;
    }
}


