package org.bugra.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.bugra.exception.UserNotFoundException;
import org.bugra.security.dto.TokenPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    private final String secret;
    private final long accessExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration
            ){
        this.accessExpiration = accessExpiration;
        this.secret = secret;
    }

    public String generateAccessToken(TokenPayload tokenPayload){
        Map<String, String> claims = new HashMap<>();

        claims.put("username", tokenPayload.username());
        long expiration = Duration.ofMinutes(accessExpiration).toMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(tokenPayload.username())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(secret))
                .compact();
    }

    private Key getKey(String key){
        byte[] bytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(bytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);

    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getKey(secret))
                .build().parseClaimsJws(token).getBody();
    }

    public boolean isValid(String token, UserDetails userDetails){
        try {
            String username = extractUsername(token);

            return userDetails.getUsername().equals(username);
        } catch (JwtException e) {
            return false;
        }
    }
}
