package org.trade.core.properties;

import java.io.IOException;
import java.io.Serial;

/**
 * @author Simon Allen
 */
public class PropertyNotFoundException extends IOException {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 6644254900539719502L;

    /**
     * Constructor for PropertyNotFoundException.
     *
     * @param message String
     */
    public PropertyNotFoundException(String message) {
        super(message);
    }
}
