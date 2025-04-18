package org.trade.core.persistent.dao.indicator.series;

public interface Dataset {
    void addChangeListener(DatasetChangeListener var1);

    void removeChangeListener(DatasetChangeListener var1);

    DatasetGroup getGroup();

    void setGroup(DatasetGroup var1);
}
