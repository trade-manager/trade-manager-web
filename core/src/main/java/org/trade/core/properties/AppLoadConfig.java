package org.trade.core.properties;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class AppLoadConfig {

    private final static String CORE_PROPERTY_FILE = "core.properties";
    private final static AppLoadConfig fAppConfigLoad = new AppLoadConfig();

    AppLoadConfig() {
    }

    /**
     * Method loadAppProperties.
     */
    public static void loadAppProperties() throws MissingPropertiesException {

        ConfigProperties.getDeploymentProperties(fAppConfigLoad, CORE_PROPERTY_FILE);
    }
}
