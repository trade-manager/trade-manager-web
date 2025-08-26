package org.trade.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.trade.core.ApplicationProfileInitializer;


/**
 * Sample configuration to bootstrap Spring Data JPA through JavaConfig
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}, scanBasePackages = {"org.trade.core", "org.trade.web"})
@SpringBootApplication(scanBasePackages = {"org.trade.core", "org.trade.web"})
@ConfigurationPropertiesScan("org.trade.core")
public class TradeApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(TradeApplication.class)
                .initializers(new ApplicationProfileInitializer())
                .run(args);
    }
}
