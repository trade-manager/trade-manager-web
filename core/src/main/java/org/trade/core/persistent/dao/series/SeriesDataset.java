package org.trade.core.persistent.dao.series;

public interface SeriesDataset extends Dataset {
    int getSeriesCount();

    Comparable getSeriesKey(int var1);

    int indexOf(Comparable var1);
}
