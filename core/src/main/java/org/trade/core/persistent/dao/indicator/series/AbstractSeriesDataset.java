package org.trade.core.persistent.dao.indicator.series;

import java.io.Serializable;

public abstract class AbstractSeriesDataset extends AbstractDataset implements SeriesDataset, SeriesChangeListener, Serializable {
    private static final long serialVersionUID = -6074996219705033171L;

    protected AbstractSeriesDataset() {
    }

    public abstract int getSeriesCount();

    public abstract Comparable getSeriesKey(int var1);

    public int indexOf(Comparable seriesKey) {
        int seriesCount = this.getSeriesCount();

        for (int s = 0; s < seriesCount; ++s) {
            if (this.getSeriesKey(s).equals(seriesKey)) {
                return s;
            }
        }

        return -1;
    }

    public void seriesChanged(SeriesChangeEvent event) {
        this.fireDatasetChanged();
    }
}

