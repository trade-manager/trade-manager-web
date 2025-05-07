package org.trade.core.persistent.dao.series;

public interface IntervalXYDataset extends XYDataset {
    Number getStartX(int var1, int var2);

    double getStartXValue(int var1, int var2);

    Number getEndX(int var1, int var2);

    double getEndXValue(int var1, int var2);

    Number getStartY(int var1, int var2);

    double getStartYValue(int var1, int var2);

    Number getEndY(int var1, int var2);

    double getEndYValue(int var1, int var2);
}
