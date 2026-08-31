package com.demo.ai_harness_demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Profile("!dev")
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	/**
	 * Registers the JWT filter used by the HTTP security chain.
	 *
	 * @param jwtService token parser and validator
	 * @return once-per-request JWT filter
	 */
	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
		return new JwtAuthenticationFilter(jwtService);
	}

	/**
	 * Configures stateless JWT security for all endpoints.
	 *
	 * @param http Spring Security HTTP builder
	 * @param jwtAuthenticationFilter JWT bearer filter
	 * @return the security filter chain
	 * @throws Exception if the chain cannot be built
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
			throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	/**
	 * Password encoder for any in-memory or future user stores.
	 *
	 * @return BCrypt encoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
