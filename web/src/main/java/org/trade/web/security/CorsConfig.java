package org.trade.web.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Configuration
public class CorsConfig {

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins}") List<String> allowedOrigins,
                                                    @Value("${app.cors.allowedMethods}") String allowedMethods,
                                                    @Value("${app.cors.allowedHeaders}") String allowedHeaders,
                                                    @Value("${app.cors.allowCredentials}") boolean allowCredentials) {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(allowCredentials);
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.addAllowedMethod(allowedMethods);
        configuration.addAllowedHeader(allowedHeaders);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}