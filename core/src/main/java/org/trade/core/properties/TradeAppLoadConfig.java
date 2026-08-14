package org.trade.core.properties;

import java.util.Properties;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TradeAppLoadConfig {

    private final static String CORE_PROPERTY_FILE = "core.properties";
    private final static TradeAppLoadConfig tradeAppLoadConfig = new TradeAppLoadConfig();

    TradeAppLoadConfig() {
    }

    /**
     * Method loadAppProperties.
     */
    public static Properties loadAppProperties() throws MissingPropertiesException {
        return ConfigProperties.getDeploymentProperties(tradeAppLoadConfig, CORE_PROPERTY_FILE);
    }
}
