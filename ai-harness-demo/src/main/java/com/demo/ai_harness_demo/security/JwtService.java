package com.demo.ai_harness_demo.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey secretKey;
	private final long expirationMs;

	public JwtService(@Value("${jwt.secret}") String secret,
			@Value("${jwt.expiration-ms}") long expirationMs) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationMs = expirationMs;
	}

	/**
	 * Issues a signed JWT for the given principal and authorities.
	 *
	 * @param username authenticated username
	 * @param authorities granted roles or privileges
	 * @return compact JWT string
	 */
	public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
		Instant now = Instant.now();
		List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).toList();
		return Jwts.builder()
				.subject(username)
				.claim("roles", roles)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusMillis(expirationMs)))
				.signWith(secretKey)
				.compact();
	}

	/**
	 * Reads the username (JWT subject) from a signed token.
	 *
	 * @param token compact JWT
	 * @return subject claim
	 */
	public String extractUsername(String token) {
		return parseClaims(token).getSubject();
	}

	/**
	 * Reads role claims from a signed token.
	 *
	 * @param token compact JWT
	 * @return Spring Security authorities
	 */
	@SuppressWarnings("unchecked")
	public List<SimpleGrantedAuthority> extractAuthorities(String token) {
		Claims claims = parseClaims(token);
		List<String> roles = claims.get("roles", List.class);
		if (roles == null) {
			return List.of();
		}
		return roles.stream().map(SimpleGrantedAuthority::new).toList();
	}

	/**
	 * Verifies signature and expiration.
	 *
	 * @param token compact JWT
	 * @return {@code true} when the token can be parsed
	 */
	public boolean isValid(String token) {
		parseClaims(token);
		return true;
	}

	private Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
