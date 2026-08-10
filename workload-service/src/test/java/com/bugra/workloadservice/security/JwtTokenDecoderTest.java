package com.bugra.workloadservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenDecoderTest {

    private JwtTokenDecoder tokenDecoder;
    private String my_secret = "QmVuaW1Db2tHaXpsaVRlc3RBbmFodGFyaW1FbkF6MjU2Qml0T2xtYWxpMTIz";

    private String createToken(String username, long exp){
            Map<String, String> claims = new HashMap<>();

            claims.put("username", username);
            long expiration = Duration.ofMinutes(exp).toMillis();

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getKey(my_secret))
                    .compact();
    }

    private Key getKey(String key){
        byte[] bytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(bytes);
    }
    @BeforeEach
    void setup(){
        tokenDecoder = new JwtTokenDecoder(my_secret);
    }

    @Test
    @DisplayName("Should return correct username")
    void extractUsername_shouldReturnUsername(){
        String accessToken = createToken("john.doe", 15L);

        String actualUsername = tokenDecoder.extractUsername(accessToken);

        assertEquals("john.doe", actualUsername);
    }

    @Test
    @DisplayName("Should return true when token is not expired")
    void isValid_shouldReturnTrueWhenNotExpired(){
        String accessToken = createToken("john.doe", 15L);

        boolean actual = tokenDecoder.isValid(accessToken);

        assertTrue(actual);
    }

    @Test
    @DisplayName("Should return false when token is expired")
    void isValid_shouldReturnFalseWhenExpired(){
        String accessToken = createToken("john.doe", -99L);

        boolean actual = tokenDecoder.isValid(accessToken);

        assertFalse(actual);
    }
}