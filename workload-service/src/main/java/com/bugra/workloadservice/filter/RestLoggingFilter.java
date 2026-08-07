package com.bugra.workloadservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class RestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // get request details
        String method = request.getMethod();
        String uri = request.getRequestURI();

        logger.info("Incoming request: {} {}", method, uri);

        try {
            filterChain.doFilter(request, response);
        } finally {
            int status = response.getStatus();
            logger.info("{} {} -> status: {}", method, uri, status);

        }
    }
}
