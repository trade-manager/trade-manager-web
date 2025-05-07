package org.trade.core.persistent.dao.series;

public abstract class AbstractXYDataset extends AbstractSeriesDataset implements XYDataset {
    public AbstractXYDataset() {
    }

    public DomainOrder getDomainOrder() {
        return DomainOrder.NONE;
    }

    public double getXValue(int series, int item) {
        double result = Double.NaN;
        Number x = this.getX(series, item);
        if (x != null) {
            result = x.doubleValue();
        }

        return result;
    }

    public double getYValue(int series, int item) {
        double result = Double.NaN;
        Number y = this.getY(series, item);
        if (y != null) {
            result = y.doubleValue();
        }

        return result;
    }
}

