package org.trade.core.persistent.tradeorder;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TradeOrderNotFoundException extends RuntimeException {

    public TradeOrderNotFoundException(String message) {
        super(message);
    }
}
