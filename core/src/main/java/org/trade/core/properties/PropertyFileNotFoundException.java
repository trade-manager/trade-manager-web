
package org.trade.core.properties;

import java.io.FileNotFoundException;
import java.io.Serial;

/**
 * @author Simon Allen
 */
public class PropertyFileNotFoundException extends FileNotFoundException {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3927055846214700908L;

    /**
     * Constructor for PropertyFileNotFoundException.
     *
     * @param message String
     */
    public PropertyFileNotFoundException(String message) {
        super(message);
    }
}
