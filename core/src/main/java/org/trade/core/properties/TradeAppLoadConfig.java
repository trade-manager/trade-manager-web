package org.trade.core.properties;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TradeAppLoadConfig {

    private final static String PROPERTIES_PROPERTY_FILE = "trade.properties";
    private final static TradeAppLoadConfig tradeAppLoadConfig = new TradeAppLoadConfig();

    TradeAppLoadConfig() {
    }

    /**
     * Method loadAppProperties.
     */
    public static Properties loadAppProperties() throws IOException {
        return ConfigProperties.getDeploymentProperties(tradeAppLoadConfig, PROPERTIES_PROPERTY_FILE);
    }
}
