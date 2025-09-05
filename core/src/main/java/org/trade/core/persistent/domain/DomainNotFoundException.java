package org.trade.core.persistent.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DomainNotFoundException extends RuntimeException {

    public DomainNotFoundException(String message) {
        super(message);
    }
}
