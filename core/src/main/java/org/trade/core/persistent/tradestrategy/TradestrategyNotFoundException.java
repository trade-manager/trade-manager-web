package org.trade.core.persistent.tradestrategy;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TradestrategyNotFoundException extends RuntimeException {

    public TradestrategyNotFoundException(String message) {
        super(message);
    }
}
