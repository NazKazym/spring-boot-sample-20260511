package com.example.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${JWT_SECRET}")
    private String secret;

    private SecretKey getSigningKey() {
        // Use StandardCharsets for consistency
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email) {
        log.info("Generating JWT token for user: {}", email);
        return Jwts.builder()
                .subject(email) // Replaces setSubject()
                .issuedAt(new Date()) // Replaces setIssuedAt()
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // Replaces setExpiration()
                .signWith(getSigningKey()) // Algorithm is now inferred from the Key type (HS256)
                .compact();
    }

    public String extractEmail(String token) {
        try {
            // parserBuilder() is now simplified back to parser()
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey()) // Replaces setSigningKey()
                    .build()
                    .parseSignedClaims(token) // Replaces parseClaimsJws()
                    .getPayload(); // Replaces getBody()

            return claims.getSubject();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("JWT token has expired");
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT signature does not match");
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("Invalid JWT format");
        } catch (Exception e) {
            log.error("Failed to extract email from token: {}", e.getMessage());
        }
        return null;
    }
}