package com.quantnexus.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Core Security Checkpoint.
 * Intercepts all traffic to validate Bearer tokens and establish a secure,
 * stateless execution context for the duration of the request.
 * @author Manish Singh
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Injects our JpaUserDetailsService
    private final JwtBlacklistService blacklistService;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/auth");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Let the request pass if there is no Bearer token
        if(authHeader == null  || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try{
            // 2. Extract the token string and the email payload
            jwt = authHeader.substring(7);

            //Check if token was manually locked : logout
            if(blacklistService.isTokenBlacklisted(jwt)){
                log.warn("Security Alert: Blocked attempt to use a revoked token.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Session terminated. Please log in again.");
                return;
            }
            userEmail = jwtService.extractUsername(jwt);

            // 3. If an email exists and the user isn't already authenticated in this thread
            if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Fetch the SecurityUser from the database
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                // 4. Validate cryptographic signature and expiration
                if(jwtService.isTokenValid(jwt, userDetails)) {

                    // 5. Establish the Trusted Context for this specific request
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 6. Lock the user into Spring's memory vault
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Security: Authenticated request for [{}]", userEmail);
                }
            }
        }catch(JwtException e){
            // Catches expired tokens or hacked signatures without crashing the server
            log.warn("Security Alert: Invalid or expired JWT token encountered. IP: {}", request.getRemoteAddr());
        }
        // 7. Hand the request off to the next filter or the target Controller
        filterChain.doFilter(request, response);
    }

}
