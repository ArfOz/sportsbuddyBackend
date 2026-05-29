package com.sportbuddy.service;

import com.sportbuddy.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshExpiration;

    private static final SecureRandom secureRandom = new SecureRandom();


    // -------------------- CREATE TOKEN --------------------
    private String createToken(Map<String, Object> claims, User user, long expirationMs) {
        return Jwts.builder()
                .claims(claims)
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignInKey())
                .compact();
    }


    // -------------------- GENERATE ACCESS TOKEN --------------------
    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();

        // Add roles
        claims.put("roles",
                user.getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .toList()
        );

        // Add email (optional)
        claims.put("email", user.getEmail());

        return createToken(claims, user, jwtExpiration);
    }


    // -------------------- GENERATE REFRESH TOKEN --------------------
    public String generateRefreshToken(User user) {
        return generateOpaqueRefreshToken();
    }

    public String generateOpaqueRefreshToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }


    // -------------------- VALIDATION --------------------
    public boolean isTokenValid(String token, User user) {
        String userId = extractUsername(token);
        return userId.equals(user.getId().toString()) && !isTokenExpired(token);
    }


    // -------------------- EXTRACT DATA --------------------
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }


    // -------------------- CLAIM HELPERS --------------------
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    // -------------------- SIGN KEY --------------------
    private javax.crypto.SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
