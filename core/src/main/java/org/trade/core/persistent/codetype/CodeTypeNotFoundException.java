package org.trade.core.persistent.codetype;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CodeTypeNotFoundException extends RuntimeException {

    public CodeTypeNotFoundException(String message) {
        super(message);
    }
}
