package org.trade.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.trade.core.persistent.role.Role;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(HttpMethod.GET, "/api/employees", "/api/employees/**").hasAnyAuthority(Role.ROLE_ADMIN, Role.ROLE_USER)
                        .requestMatchers(HttpMethod.GET, "/api/users/me").hasAnyAuthority(Role.ROLE_ADMIN, Role.ROLE_USER)
                        .requestMatchers("/api/employees", "/api/employees/**").hasAuthority(Role.ROLE_ADMIN)
                        .requestMatchers("/api/users", "/api/users/**").hasAuthority(Role.ROLE_ADMIN)
                        .requestMatchers("/public/**", "/auth/**").permitAll()
                        .requestMatchers("/", "/error", "/csrf", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
