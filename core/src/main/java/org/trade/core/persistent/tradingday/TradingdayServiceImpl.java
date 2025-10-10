package org.trade.core.persistent.tradingday;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import org.trade.core.persistent.tradestrategy.Tradestrategy;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Service
public class TradingdayServiceImpl implements TradingdayService {

    @PersistenceContext
    private EntityManager entityManager;

    private final TradingdayRepository tradingdayRepository;

    public TradingdayServiceImpl(final TradingdayRepository tradingdayRepository) {

        this.tradingdayRepository = tradingdayRepository;
    }

    /**
     * Method findTradingdaysByDateRange.
     *
     * @param startDate ZonedDateTime
     * @param endDate   ZonedDateTime
     * @return Tradingdays
     */
    public Tradingdays findTradingdaysByDateRangeOrderByOpenAsc(ZonedDateTime startDate, ZonedDateTime endDate) {

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
                tradestrategy.getPortfolio().getAccounts().size();
                tradestrategy.getStrategy().getIndicatorSeries().size();
            }
        }

        return tradingdays;
    }

    /**
     * Method findByOpenCloseDateOrderByOpenAsc.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return Tradingday
     */
    public Tradingday findByOpenCloseDateOrderByOpenAsc(ZonedDateTime openDate, ZonedDateTime closeDate) {

        List<Tradingday> items = tradingdayRepository.findByOpenAndCloseOrderByOpenAsc(openDate, closeDate);

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
     * Method findTradingdayByOpenDate.
     *
     * @param openDate  ZonedDateTime
     * @param closeDate ZonedDateTime
     * @return Tradingday
     */
    public Tradingday findByOpenCloseDate(ZonedDateTime openDate, ZonedDateTime closeDate) {

        List<Tradingday> items = tradingdayRepository.findByOpenAndCloseOrderByOpenAsc(openDate, closeDate);

        if (!items.isEmpty()) {

            return items.getFirst();
        }

        return null;
    }

    public Tradingday findById(final Long tradingdayId) {

        return tradingdayRepository.findById(tradingdayId).isPresent() ? tradingdayRepository.findById(tradingdayId).get() : null;
    }
}


