package com.group13.ecopark_bicycle_parking.account.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    private SecretKey key() { return Keys.hmacShaKeyFor(secret.getBytes()); }

    public String generateToken(UserDetails u) {
        return Jwts.builder()
                .subject(u.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key()).compact();
    }

    public String extractEmail(String token) { return extractClaim(token, Claims::getSubject); }

    public boolean validateToken(String token, UserDetails u) {
        return extractEmail(token).equals(u.getUsername())
                && !extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> fn) {
        return fn.apply(Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload());
    }
}
