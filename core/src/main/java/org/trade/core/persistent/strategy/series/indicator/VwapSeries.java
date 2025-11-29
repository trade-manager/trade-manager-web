package org.trade.core.persistent.strategy.series.indicator;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import org.trade.core.persistent.ServiceException;
import org.trade.core.persistent.strategy.Strategy;
import org.trade.core.persistent.strategy.series.indicator.vwap.VwapItem;
import org.trade.core.util.time.RegularTimePeriod;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

/**
 * Volume-Weighted Average Price (VWAP) is exactly what it sounds like: the
 * average price weighted by volume. VWAP equals the dollar value of all trading
 * periods divided by the total trading volume for the current day. Calculation
 * starts when trading opens and ends when trading closes. Because it is good
 * for the current trading day only, intraday periods and data are used in the
 * calculation.
 * <p>
 * Cumulative(Volume x Typical Price)/Cumulative(Volume)
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@DiscriminatorValue("VwapSeries")
public class VwapSeries extends IndicatorSeries {

    @Serial
    private static final long serialVersionUID = 20183087035446657L;

    /**
     * Creates a new empty series. By default, items added to the series will be
     * sorted into ascending order by period, and duplicate periods will not be
     * allowed.
     *
     * @param strategy       Strategy
     * @param name           String
     * @param type           String
     * @param description    String
     * @param displayOnChart Boolean
     * @param chartRGBColor  Integer
     * @param subChart       Boolean
     */
    public VwapSeries(Strategy strategy, String name, String type, String description, Boolean displayOnChart,
                      Integer chartRGBColor, Boolean subChart) {
        super(strategy, name, type, description, displayOnChart, chartRGBColor, subChart);
    }

    public VwapSeries() {
        super(Type.VwapSeries.getType());
    }

    /**
     * Returns the time period for the specified item.
     *
     * @param index the item index.
     * @return The time period.
     */
    public RegularTimePeriod getPeriod(Integer index) {
        final VwapItem item = (VwapItem) getDataItem(index);
        return item.getPeriod();
    }

    /**
     * Adds a data item to the series.
     *
     * @param period    the period.
     * @param vwapPrice BigDecimal
     */
    public void add(RegularTimePeriod period, BigDecimal vwapPrice) throws ServiceException {
        if (!this.isEmpty()) {
            VwapItem item0 = (VwapItem) this.getDataItem(0);
            if (!period.getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(new VwapItem(period, vwapPrice), true);
    }

    /**
     * Adds a data item to the series.
     *
     * @param notify   the notify listeners.
     * @param dataItem VwapItem
     */
    public void add(VwapItem dataItem, Boolean notify) throws ServiceException {
        if (!this.isEmpty()) {
            VwapItem item0 = (VwapItem) this.getDataItem(0);
            if (!dataItem.getPeriod().getClass().equals(item0.getPeriod().getClass())) {
                throw new IllegalArgumentException("Can't mix RegularTimePeriod class types.");
            }
        }
        super.add(dataItem, notify);
    }

    /**
     * Method createSeries.
     *
     * @param source      CandleDataset
     * @param seriesIndex int
     */
    public void createSeries(CandleDataset source, Integer seriesIndex) throws ServiceException {

        if (source.getSeries(seriesIndex) == null) {
            throw new IllegalArgumentException("Null source (XYDataset).");
        }

        for (int i = 0; i < source.getSeries(seriesIndex).getItemCount(); i++) {
            this.updateSeries(source.getSeries(seriesIndex), i, true);
        }
    }

    /**
     * Method updateSeries.
     *
     * @param source CandleSeries
     * @param skip   int
     * @param newBar boolean
     */
    public void updateSeries(CandleSeries source, Integer skip, Boolean newBar) throws ServiceException {

        if (source == null) {
            throw new IllegalArgumentException("Null source (CandleSeries).");
        }

        if (source.getItemCount() > skip) {

            /*
             * If the item does not exist in the series then this is a new time
             * period and so we need to remove the last in the set and add the
             * new periods values. Otherwise we just update the last value in
             * the set.
             */
            if (newBar) {
                VwapItem dataItem = new VwapItem(source.getRollingCandle().getPeriod(),
                        BigDecimal.valueOf(source.getRollingCandle().getVwap()));
                this.add(dataItem, false);
            } else {
                VwapItem dataItem = (VwapItem) this.getDataItem(this.getItemCount() - 1);
                dataItem.setVwapPrice(source.getRollingCandle().getVwap());
            }
        }
    }

    /**
     * Method printSeries.
     */
    public void printSeries() {
        for (int i = 0; i < this.getItemCount(); i++) {
            VwapItem dataItem = (VwapItem) this.getDataItem(i);
            _log.debug("Type: {} Time: {} Value: {}", this.getType(), dataItem.getPeriod().getStart(), dataItem.getVwapPrice());
        }
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Transient
    public List<Object> getParam(String type) {

        return super.getParam(type);
    }
}
