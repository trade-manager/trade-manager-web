package org.trade.core.properties;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class AppLoadConfig {

    private static final String CORE_PROPERTY_FILE = "core.properties";
    private static final AppLoadConfig fAppConfigLoad = new AppLoadConfig();

    AppLoadConfig() {
    }

    /**
     * Method loadAppProperties.
     */
    public static void loadAppProperties() throws MissingPropertiesException {

        ConfigProperties.getDeploymentProperties(fAppConfigLoad, CORE_PROPERTY_FILE);
    }
}
