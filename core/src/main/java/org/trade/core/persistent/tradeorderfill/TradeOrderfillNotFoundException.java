package org.trade.core.persistent.tradeorderfill;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TradeOrderfillNotFoundException extends RuntimeException {

    public TradeOrderfillNotFoundException(String message) {
        super(message);
    }
}
