package org.trade.core.persistent.strategy.series;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface Dataset {
    void addChangeListener(DatasetChangeListener var1);

    void removeChangeListener(DatasetChangeListener var1);

    DatasetGroup getGroup();

    void setGroup(DatasetGroup var1);
}
