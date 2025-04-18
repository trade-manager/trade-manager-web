package org.trade.core.persistent.dao.indicator.series;

import java.util.EventListener;

public interface DatasetChangeListener extends EventListener {
    void datasetChanged(DatasetChangeEvent var1);
}
