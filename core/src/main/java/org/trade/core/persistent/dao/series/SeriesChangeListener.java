package org.trade.core.persistent.dao.series;


import java.util.EventListener;

public interface SeriesChangeListener extends EventListener {
    void seriesChanged(SeriesChangeEvent var1);
}
