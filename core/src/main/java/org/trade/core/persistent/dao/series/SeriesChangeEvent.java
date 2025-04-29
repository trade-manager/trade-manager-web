package org.trade.core.persistent.dao.series;

import java.io.Serial;
import java.io.Serializable;
import java.util.EventObject;

public class SeriesChangeEvent extends EventObject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1593866085210089052L;

    public SeriesChangeEvent(Object source) {
        super(source);
    }
}
