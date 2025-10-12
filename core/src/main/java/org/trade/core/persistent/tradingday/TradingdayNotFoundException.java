package org.trade.core.persistent.tradingday;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TradingdayNotFoundException extends RuntimeException {

    public TradingdayNotFoundException(String message) {
        super(message);
    }
}
