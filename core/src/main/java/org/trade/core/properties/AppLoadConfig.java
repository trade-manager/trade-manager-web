package org.trade.core.properties;

import java.io.IOException;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class AppLoadConfig {
    private final static String PROPERTIES_PROPERTY_FILE = "core.properties";
    private final static AppLoadConfig m_AppConfigLoad = new AppLoadConfig();

    AppLoadConfig() {
    }

    /**
     * Method loadAppProperties.
     */
    public static void loadAppProperties() throws IOException {
        ConfigProperties.getDeploymentProperties(m_AppConfigLoad, PROPERTIES_PROPERTY_FILE);
    }
}
