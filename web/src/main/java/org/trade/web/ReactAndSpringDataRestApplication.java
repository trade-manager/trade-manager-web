package org.trade.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.trade.core.ApplicationContextProvider;


/**
 * Sample configuration to bootstrap Spring Data JPA through JavaConfig
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootApplication(scanBasePackages = {"org.trade.core"})
public class ReactAndSpringDataRestApplication {


    public static void main(String[] args) {

        ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(ReactAndSpringDataRestApplication.class)
                .headless(false).run(args);
        ApplicationContextProvider applicationContextProvider = applicationContext.getBean(ApplicationContextProvider.class);
    }
}
