package org.trade.core.persistent.dao.series.indicator.candle;

import org.trade.core.persistent.dao.series.XYDataset;

public interface OHLCDataset extends XYDataset {
    Number getHigh(int var1, int var2);

    double getHighValue(int var1, int var2);

    Number getLow(int var1, int var2);

    double getLowValue(int var1, int var2);

    Number getOpen(int var1, int var2);

    double getOpenValue(int var1, int var2);

    Number getClose(int var1, int var2);

    double getCloseValue(int var1, int var2);

    Number getVolume(int var1, int var2);

    double getVolumeValue(int var1, int var2);
}
