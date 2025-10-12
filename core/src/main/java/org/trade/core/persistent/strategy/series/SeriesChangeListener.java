package org.trade.core.persistent.strategy.series;

import java.util.EventListener;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface SeriesChangeListener extends EventListener {
    void seriesChanged(SeriesChangeEvent var1);
}
