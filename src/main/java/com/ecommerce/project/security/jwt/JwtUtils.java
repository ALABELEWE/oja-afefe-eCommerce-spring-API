package com.ecommerce.project.security.jwt;

import com.ecommerce.project.security.services.UserDetailImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;


@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.ecom.app.jwtCookieName}")
    private String jwtCookie;

    /**
     * Extracts the JWT token from an HTTP cookie in the request.
     * This method is used by AuthTokenFilter if you choose to send JWTs via cookies.
     *
     * @param request The HttpServletRequest object.
     * @return The JWT string if found in the specified cookie, otherwise null.
     */

    public String getJwtFromCookie(HttpServletRequest request) {
        // WebUtils.getCookie is a convenient Spring utility to get a Cookie by its name.
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if(cookie != null){
            return cookie.getValue();
        }else {
            return null;
        }
    }
    public String getJwtFromHeader(HttpServletRequest request) {
        // WebUtils.getCookie is a convenient Spring utility to get a Cookie by its name.
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
    /**
     * Generates a JWT token and wraps it in a Spring ResponseCookie object.
     * This is typically used during the login process to send the JWT back to the client as a cookie.
     *
     * @param userPrincipal The UserDetailImpl object of the authenticated user.
     * @return A ResponseCookie containing the JWT token.
     */
    public ResponseCookie generateJwtCookie(UserDetailImpl userPrincipal){
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
                .path("/api")
                .maxAge(24*60*60)
                .httpOnly(false)
                .build();
        return cookie;
    }


    /**
     * Generates a "clean" (empty) JWT cookie, typically used for logging out a user.
     * This tells the browser to effectively remove the JWT cookie.
     *
     * @return A ResponseCookie that invalidates the JWT cookie.
     */
    public ResponseCookie getCleanJwtCookie(){
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .build();
        return cookie;
    }

    /**
     * Generates a new JWT token string for a given username.
     * This is the core method for creating the JWT itself.
     *
     * @param username The username for whom the token is being generated.
     * @return The signed JWT string.
     */
    //Generate token from username
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime()+jwtExpirationMs))
                .signWith(key())
                .compact();
    }


    /**
     * Extracts the username (subject) from a given JWT token.
     * This is used by AuthTokenFilter to identify the user from the incoming token.
     *
     * @param token The JWT token string.
     * @return The username extracted from the token's subject claim.
     */
    //Getting username from JWT token
    public String getUsernameFromJWTToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Generates a secure signing key from the Base64-encoded JWT secret.
     * This key is essential for both signing (generating) and verifying JWTs.
     *
     * @return A secure cryptographic Key object.
     */
    //Generate Signing key
    public Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    /**
     * Validates a given JWT token.
     * This method checks if the token's signature is valid and if it has expired.
     *
     * @param token The JWT token string to validate.
     * @return true if the token is valid, false otherwise.
     */

    //Validate JWT token
    public boolean validateJwtToken(String token) {
        try{
            System.out.println("Validate");
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        }catch (ExpiredJwtException e){
            logger.error("Expired JWT token: {}", e.getMessage());
        }catch (UnsupportedJwtException e){
            logger.error("Unsupported JWT token: {}", e.getMessage());
        }catch (IllegalArgumentException e){
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

}
