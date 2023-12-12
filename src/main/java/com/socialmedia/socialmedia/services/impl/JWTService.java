package com.socialmedia.socialmedia.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;

@Service
public class JWTService {
    private final byte[] authSecret;
    private final byte[] refreshSecret;
    private final byte[] ticketSecret;

    JWTService(@Value("${jwt.auth-secret}") String authStr,
               @Value("${jwt.refresh-secret}") String refreshStr,
               @Value("${jwt.ticket-secret}") String ticketStr) {
        try {
            this.authSecret = Base64.getDecoder().decode(authStr);
            this.refreshSecret = Base64.getDecoder().decode(refreshStr);
            this.ticketSecret = Base64.getDecoder().decode(ticketStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Error decoding Base64 secret: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw the exception to prevent the bean from being created
        }
    }


    public String generateAccessToken(Long userId) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expiration = now.plusHours(1);
        return  Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(expiration.toInstant()))
                .signWith(Keys.hmacShaKeyFor(authSecret))
                .compact();
    }

    public String generateRefreshToken(Long userId) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expiration = now.plusDays(1);
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(now.toInstant()))
                // 24 hours expiration.
                .expiration(Date.from(expiration.toInstant()))
                .signWith(Keys.hmacShaKeyFor(refreshSecret))
                .compact();
    }

    public String generateTicket(HttpServletRequest request, Long userId) {
        ZonedDateTime now = ZonedDateTime.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("ip", request.getRemoteAddr())
                .issuedAt(Date.from(now.toInstant()))
                .signWith(Keys.hmacShaKeyFor(ticketSecret))
                .compact();
    }

    public Long getUserIdFromAccessToken(String token){
        return getUserIdFromToken(token, authSecret);
    }

    public Long getUserIdFromRefreshToken(String token){
        return getUserIdFromToken(token, refreshSecret);
    }

    public TokenStatus validateAccessToken(String token) {
        return validateToken(token, authSecret);
    }

    public TokenStatus validateTicket(String token) {
        return validateToken(token, ticketSecret);
    }

    public String[] getUserIdAndIpFromTicket(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(ticketSecret)).build().parseSignedClaims(token).getPayload();
            return new String[]{claims.getSubject(), claims.get("ip", String.class)};
        } catch (ExpiredJwtException e) {
            System.out.println("Ticket expired");
        } catch (Exception e) {
            System.out.println("Invalid ticket");
        }
        return null;
    }

    public TokenStatus validateRefreshToken(String token) {
        return validateToken(token, refreshSecret);
    }

    private TokenStatus validateToken(String token, byte[] secret) {
        try {
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret)).build().parse(token);
            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired");
            return TokenStatus.EXPIRED;
        } catch (Exception e) {
            System.out.println("Invalid token");
            return TokenStatus.INVALID;
        }
    }

    public enum TokenStatus {
        VALID,
        EXPIRED,
        INVALID
    }

    private Long getUserIdFromToken(String token, byte[] authSecret) {
        try {
            Claims claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(authSecret)).build().parseSignedClaims(token).getPayload();
            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired");
        } catch (Exception e) {
            System.out.println("Invalid token");
        }
        return null;
    }

    public void setBothCookies(HttpServletResponse res, Long userId) {
        setRefreshCookie(res, userId);
        setAccessTokenCookie(res, userId);
    }

    public void disableCookies(HttpServletResponse res){
        Cookie refreshTokenCookie = new Cookie("refresh_token", "");
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setDomain("localhost");
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setAttribute("SameSite", "None");
        res.addCookie(refreshTokenCookie);
        Cookie accessTokenCookie = new Cookie("access_token", "");
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setDomain("localhost");
        accessTokenCookie.setPath("/");
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setAttribute("SameSite", "None");
        res.addCookie(accessTokenCookie);
    }

    // TODO: Test the secure flag over HTTPS. Some browsers may not allow it over HTTP.
    private void setRefreshCookie(HttpServletResponse res, Long userId) {
        String refreshToken = generateRefreshToken(userId);
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setMaxAge(60 * 60 * 24);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setDomain("localhost");
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setAttribute("SameSite", "None");
        res.addCookie(refreshTokenCookie);
    }

    public void setAccessTokenCookie(HttpServletResponse res, Long userId) {
        String accessToken = generateAccessToken(userId);
        Cookie accessTokenCookie = new Cookie("access_token", accessToken);
        accessTokenCookie.setMaxAge(60 * 60);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setDomain("localhost");
        accessTokenCookie.setPath("/");
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setAttribute("SameSite", "None");
        res.addCookie(accessTokenCookie);
    }

}
