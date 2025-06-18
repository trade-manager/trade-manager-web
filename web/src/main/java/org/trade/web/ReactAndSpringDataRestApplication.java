package org.trade.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * Sample configuration to bootstrap Spring Data JPA through JavaConfig
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@SpringBootApplication(scanBasePackages = {"org.trade.core"})
@ComponentScan({"org.trade.core"})
@EntityScan("org.trade.core")
@EnableJpaRepositories("org.trade.core")
public class ReactAndSpringDataRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactAndSpringDataRestApplication.class, args);
    }
}
