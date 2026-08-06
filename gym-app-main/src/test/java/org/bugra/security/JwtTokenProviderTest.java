package org.bugra.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.bugra.model.User;
import org.bugra.security.dto.TokenPayload;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {


    private JwtTokenProvider jwtTokenProvider;
    private String my_secret = "QmVuaW1Db2tHaXpsaVRlc3RBbmFodGFyaW1FbkF6MjU2Qml0T2xtYWxpMTIz";
    private long accessExpiration = 15;

    @BeforeEach
    void setup(){
        jwtTokenProvider = new JwtTokenProvider(my_secret, accessExpiration);
    }

    @Test
    @DisplayName("Should generate token")
    void generateAccessToken_shouldGenerateToken() {
        TokenPayload tokenPayload = new TokenPayload(
                "john.doe"
        );
        String accessToken = jwtTokenProvider.generateAccessToken(tokenPayload);

        assertNotNull(accessToken);
        assertFalse(accessToken.isBlank());
    }

    @Test
    @DisplayName("Should return username")
    void extractUsername_shouldReturnUsername(){
        TokenPayload tokenPayload = new TokenPayload(
                "john.doe"
        );
        String accessToken = jwtTokenProvider.generateAccessToken(tokenPayload);

        String actualUsername = jwtTokenProvider.extractUsername(accessToken);

        assertEquals("john.doe", actualUsername);
    }

    @Test
    @DisplayName("Should return true when token is not expired")
    void isValid_shouldReturnTrueWhenNotExpired(){
        TokenPayload tokenPayload = new TokenPayload(
                "john.doe"
        );
        String accessToken = jwtTokenProvider.generateAccessToken(tokenPayload);

        User user = new User();
        user.setUsername("john.doe");

        UserDetails userDetails = new UserPrincipal(user);

        boolean actual = jwtTokenProvider.isValid(accessToken, userDetails);

        assertEquals(true, actual);
    }

    @Test
    @DisplayName("Should return false when token is expired")
    void isValid_shouldReturnFalseWhenExpired(){
        TokenPayload tokenPayload = new TokenPayload(
                "john.doe"
        );

        User user = new User();
        user.setUsername("john.doe");

        UserDetails userDetails = new UserPrincipal(user);
        jwtTokenProvider = new JwtTokenProvider(my_secret, -99L);

        String accessToken = jwtTokenProvider.generateAccessToken(tokenPayload);

        boolean actual = jwtTokenProvider.isValid(accessToken, userDetails);

        assertFalse(actual);

    }

    @Test
    @DisplayName("Should return false when username is invalid")
    void isValid_shouldReturnFalseWhenUsernameNotExist(){
        TokenPayload tokenPayload = new TokenPayload(
                "john.doe"
        );

        User user = new User();
        user.setUsername("jane.smith");

        UserDetails userDetails = new UserPrincipal(user);

        String accessToken = jwtTokenProvider.generateAccessToken(tokenPayload);

        boolean actual = jwtTokenProvider.isValid(accessToken, userDetails);

        assertFalse(actual);
    }
}