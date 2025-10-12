package org.trade.core.persistent.candle;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CandleNotFoundException extends RuntimeException {

    public CandleNotFoundException(String message) {
        super(message);
    }
}
