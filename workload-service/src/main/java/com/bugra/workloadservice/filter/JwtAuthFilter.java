package com.bugra.workloadservice.filter;

import com.bugra.workloadservice.security.JwtTokenDecoder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtTokenDecoder tokenDecoder;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if(path.contains("/h2-console")){
            filterChain.doFilter(request,response);
            return;
        }

        try{
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            // extract token
            String accessToken = authHeader.substring(7);
            String username = tokenDecoder.extractUsername(accessToken);

            // check username is found and if the user is already authenticated
            if(StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null){
                // token validation
                if(tokenDecoder.isValid(accessToken)){
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    null
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request,response);

        }catch (Exception e){
            logger.error("Auth Error: {}", e.getMessage());
            request.setAttribute("jwt_error", e.getMessage());
            filterChain.doFilter(request, response);
        }
    }

}