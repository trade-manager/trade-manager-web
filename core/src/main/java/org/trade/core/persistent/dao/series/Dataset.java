package org.trade.core.persistent.dao.series;

public interface Dataset {
    void addChangeListener(DatasetChangeListener var1);

    void removeChangeListener(DatasetChangeListener var1);

    DatasetGroup getGroup();

    void setGroup(DatasetGroup var1);
}
