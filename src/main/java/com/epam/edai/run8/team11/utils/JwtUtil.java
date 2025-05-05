package com.epam.edai.run8.team11.utils;

import com.epam.edai.run8.team11.dto.user.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey key;
    private static final long EXPIRATION_TIME = 86400000; // 1 day

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        // For JJWT 0.12.5, the secret should be Base64-encoded
        // If your secret is not Base64-encoded, you can use:
        byte[] keyBytes = secret.getBytes();
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDto user) {
        log.info("User -> {}", user);
        String jwtToken = Jwts.builder()
                .subject(user.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();

        log.info("Token -> {}", jwtToken);
        return jwtToken;
    }

    public String extractUser(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}