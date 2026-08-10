package com.bugra.workloadservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.function.Function;

@Component
public class JwtTokenDecoder {

    private final String secret;

    public JwtTokenDecoder(
            @Value("${jwt.secret}") String secret
    ){
        this.secret = secret;
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
