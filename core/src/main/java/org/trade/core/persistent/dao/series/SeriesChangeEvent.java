package org.trade.core.persistent.dao.series;

import java.io.Serial;
import java.io.Serializable;
import java.util.EventObject;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class SeriesChangeEvent extends EventObject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1593866085210089052L;

    public SeriesChangeEvent(Object source) {
        super(source);
    }
}
