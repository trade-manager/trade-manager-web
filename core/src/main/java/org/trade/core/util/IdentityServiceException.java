
package org.trade.core.util;

import org.trade.core.exception.NestingException;

import java.io.Serial;

/**
 * This class servers as a general purpose exception for the identity service
 * component.
 *
 * @author Simon Allen
 */
public class IdentityServiceException extends NestingException {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5500569043989306886L;

    public IdentityServiceException() {
        super();
    }

    /**
     * Constructor for IdentityServiceException.
     *
     * @param t Throwable
     */
    public IdentityServiceException(Throwable t) {
        super(t);
    }

    /**
     * Constructor for IdentityServiceException.
     *
     * @param message String
     */
    public IdentityServiceException(String message) {
        super(message);
    }
}
