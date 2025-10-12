package org.trade.core.persistent.strategy.series.indicator.volume;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class Volume implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 7644763985378994305L;

    private Long volume;
    private boolean side = false;

    public Volume() {
    }

    /**
     * Constructor for Volume.
     *
     * @param volume Long
     * @param side   boolean
     */
    public Volume(Long volume, boolean side) {
        this.volume = volume;
        this.side = side;
    }

    /**
     * Method getVolume.
     *
     * @return Long
     */
    public Long getVolume() {
        return this.volume;
    }

    /**
     * Method setVolume.
     *
     * @param volume Long
     */
    public void setVolume(Long volume) {
        this.volume = volume;
    }

    /**
     * Method isSide.
     *
     * @return boolean
     */
    public boolean isSide() {
        return this.side;
    }

    /**
     * Method setSide.
     *
     * @param side boolean
     */
    public void setSide(boolean side) {
        this.side = side;
    }
}
