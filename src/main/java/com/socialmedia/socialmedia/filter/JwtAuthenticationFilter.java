package com.socialmedia.socialmedia.filter;

import com.socialmedia.socialmedia.services.impl.JWTService;
import com.socialmedia.socialmedia.services.impl.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final MyUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JWTService jwtService, MyUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (shouldNotFilter(requestURI) || request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }
            String[] tokens = extractToken(request);
            if (tokens[0] == null && tokens[1] != null) {
                if (jwtService.validateRefreshToken(tokens[1]) == JWTService.TokenStatus.VALID) {
                    // Access token is expired, but refresh token is still valid.
                    // Generate a new access token and send it back to the client.
                    Long userId = jwtService.getUserIdFromRefreshToken(tokens[1]);
                    jwtService.setAccessTokenCookie(response, userId);
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    handleInvalidToken(response);
                    return;
                }
            } else
            if (tokens[1] == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // TODO: Client has to log in again. Read the response in the client
                //  and redirect to the login page.
                response.getWriter().write("No Refresh token found.");
                return;
            }
            JWTService.TokenStatus status = jwtService.validateAccessToken(tokens[0]);
            if (status == JWTService.TokenStatus.EXPIRED) {
                handleExpiredToken(response);
                return;
            } else if (status == JWTService.TokenStatus.INVALID) {
                handleInvalidToken(response);
                return;
            }
            Long userId = jwtService.getUserIdFromAccessToken(tokens[0]);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        }


        private String[] extractToken (HttpServletRequest request){
            if (request.getCookies() == null) {
                return new String[2];
            }
            Cookie[] cookies = request.getCookies();
            String[] tokens = new String[2];
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access_token")) {
                    tokens[0] = cookie.getValue();
                } else if (cookie.getName().equals("refresh_token")) {
                    tokens[1] = cookie.getValue();
                }
            }
            return tokens;
        }

        private void handleExpiredToken (HttpServletResponse response) throws IOException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // Client has to get a new access token through the refresh token.
            response.getWriter().write("Expired token.");
        }

        private void handleInvalidToken (HttpServletResponse response) throws IOException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // Client has to log in again.
            response.getWriter().write("Invalid token.");
        }


    private boolean shouldNotFilter(String requestURI) {
        return requestURI.equals("/api/login") || requestURI.equals("/api/register") || requestURI.equals("/api/forgotPassword") || requestURI.equals("/api/test");
    }
}

