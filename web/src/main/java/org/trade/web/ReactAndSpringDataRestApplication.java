package org.trade.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
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
@SpringBootApplication(scanBasePackages = {"org.trade.core"})
@ConfigurationPropertiesScan("org.trade.core")
public class ReactAndSpringDataRestApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(ReactAndSpringDataRestApplication.class)
                .initializers(new ApplicationProfileInitializer())
                .run(args);

    }
}
