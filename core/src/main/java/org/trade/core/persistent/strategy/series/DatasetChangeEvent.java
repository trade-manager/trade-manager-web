package org.trade.core.persistent.strategy.series;

import java.util.EventObject;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class DatasetChangeEvent extends EventObject {
    private Dataset dataset;

    public DatasetChangeEvent(Object source, Dataset dataset) {
        super(source);
        this.dataset = dataset;
    }

    public Dataset getDataset() {
        return this.dataset;
    }
}
