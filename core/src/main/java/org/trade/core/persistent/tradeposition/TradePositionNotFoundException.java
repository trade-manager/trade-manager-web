package org.trade.core.persistent.tradeposition;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TradePositionNotFoundException extends RuntimeException {

    public TradePositionNotFoundException(String message) {
        super(message);
    }
}
