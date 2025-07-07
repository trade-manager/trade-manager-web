package org.trade.core;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;
import java.util.Map;

public class ApplicationProfileInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        Map<String, Object> properties = new HashMap<>();
        // properties.put("spring.main.web-application-type", "none");
        // properties.put("java.awt.headless", false);
        // environment.getPropertySources().addFirst(new MapPropertySource("tradeProperties", properties));
        environment.addActiveProfile("getInitializeConfig");
    }
}