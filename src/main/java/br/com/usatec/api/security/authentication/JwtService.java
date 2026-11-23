package br.com.usatec.api.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

	private final SecretKey key;
	private final long accessExpirationSeconds;
	private final long refreshExpirationSeconds;

	public JwtService(
			@Value("${security.token.secret}") String secret,
			@Value("${security.jwt.access-expiration:900}") long accessExpirationSeconds,
			@Value("${security.jwt.refresh-expiration:604800}") long refreshExpirationSeconds
	) {
		if (secret == null || secret.length() < 32) {
			throw new IllegalStateException("security.token.secret must contain at least 32 characters");
		}
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.accessExpirationSeconds = accessExpirationSeconds;
		this.refreshExpirationSeconds = refreshExpirationSeconds;
	}

	public String generateAccessToken(CustomUserDetails user) {
		return generateToken(user, "access", accessExpirationSeconds);
	}

	public String generateRefreshToken(CustomUserDetails user) {
		return generateToken(user, "refresh", refreshExpirationSeconds);
	}

	private String generateToken(CustomUserDetails user, String type, long expirationSeconds) {
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(user.getUsername())
				.claim("uid", user.getId().toString())
				.claim("type", type)
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusSeconds(expirationSeconds)))
				.signWith(key)
				.compact();
	}

	public String getSubject(String token) {
		return parseClaims(token).getSubject();
	}

	public UUID getUserId(String token) {
		return UUID.fromString(parseClaims(token).get("uid", String.class));
	}

	public boolean isRefreshToken(String token) {
		return "refresh".equals(parseClaims(token).get("type", String.class));
	}

	public boolean isAccessToken(String token) {
		return "access".equals(parseClaims(token).get("type", String.class));
	}

	public boolean isValid(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser().verifyWith(key).build()
				.parseSignedClaims(token)
				.getPayload();
	}
}
