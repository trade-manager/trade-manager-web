package org.trade.core;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.trade.core.properties.TradeAppLoadConfig;

import java.io.IOException;
import java.util.Properties;

/**
 * Sample configuration to bootstrap Spring Data JPA through JavaConfig
 *
 * @author Simon Allen
 */
@SpringBootApplication(scanBasePackages = {"org.trade.core"})
public class ApplicationRepositoryConfig {

    @Bean
    @Profile("getInitializeConfig")
    public Properties getInitializeConfig() throws IOException {

        return TradeAppLoadConfig.loadAppProperties();
    }
}
