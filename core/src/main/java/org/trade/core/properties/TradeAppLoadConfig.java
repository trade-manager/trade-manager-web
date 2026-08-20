package org.trade.core.properties;

import java.util.Properties;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TradeAppLoadConfig {

    private static final String CORE_PROPERTY_FILE = "core.properties";
    private static final TradeAppLoadConfig fTradeAppLoadConfig = new TradeAppLoadConfig();

    TradeAppLoadConfig() {
    }

    /**
     * Method loadAppProperties.
     */
    public static Properties loadAppProperties() throws MissingPropertiesException {

        return ConfigProperties.getDeploymentProperties(fTradeAppLoadConfig, CORE_PROPERTY_FILE);
    }
}
