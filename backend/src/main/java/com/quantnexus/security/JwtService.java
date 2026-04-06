package com.quantnexus.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Enterprise Cryptography Service for JSON Web Tokens.
 * Handles signing, validation, and custom claim injection (Role, UserID)
 * to ensure downstream microservices can operate statelessly.
 * @author Manish Singh
 */
@Service
public class JwtService {

    @Value("${app.security.jwt.secret-key}")
    private String secretKey;

    @Value("${app.security.jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Automatically injects the internal UserID and Role
     * into the token so the frontend and other services don't have to guess.
     */
    public String generateToken(SecurityUser securityUser) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", securityUser.getId());

        // Extract the role (e.g., "ROLE_ADMIN") and add it to the token
        String role = securityUser.getAuthorities().iterator().next().getAuthority();
        extraClaims.put("role", role);

        return buildToken(extraClaims, securityUser, jwtExpiration);

    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
         return Jwts.parser()
                 .verifyWith(getSignInKey())
                 .build()
                 .parseSignedClaims(token)
                 .getPayload();
    }

    public String extractJti(String token){
        return extractClaim(token, Claims::getId);
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
