package com.demo.ai_harness_demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Development security configuration that bypasses authentication.
 * Active only when the "dev" profile is set.
 * 
 * <p>This allows local development without needing to log in.
 * Never use this profile in production.</p>
 */
@Configuration
@Profile("dev")
public class DevSecurityConfig {

    /**
     * Configures security to permit all requests without authentication.
     * 
     * @param http the HttpSecurity to configure
     * @return the SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }
}