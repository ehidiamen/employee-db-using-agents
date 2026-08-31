package com.demo.ai_harness_demo.security;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	/**
	 * Authenticates the request when a Bearer JWT is present; otherwise leaves the security context unchanged.
	 *
	 * @param request HTTP request
	 * @param response HTTP response
	 * @param filterChain remaining filters
	 * @throws ServletException if the filter chain fails
	 * @throws IOException if the filter chain fails
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
			try {
				if (jwtService.isValid(token)) {
					String username = jwtService.extractUsername(token);
					List<SimpleGrantedAuthority> authorities = jwtService.extractAuthorities(token);
					UsernamePasswordAuthenticationToken authentication =
							new UsernamePasswordAuthenticationToken(username, null, authorities);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			} catch (RuntimeException ex) {
				log.warn("Rejected invalid JWT");
				SecurityContextHolder.clearContext();
			}
		}
		filterChain.doFilter(request, response);
	}
}
