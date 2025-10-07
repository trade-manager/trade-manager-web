package org.trade.core.persistent.rule;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RuleNotFoundException extends RuntimeException {

    public RuleNotFoundException(String message) {
        super(message);
    }
}
