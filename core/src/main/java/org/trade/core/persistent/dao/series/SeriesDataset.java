package org.trade.core.persistent.dao.series;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface SeriesDataset extends Dataset {
    int getSeriesCount();

    Comparable getSeriesKey(int var1);

    int indexOf(Comparable var1);
}
