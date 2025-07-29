package com.ecommerce.project.security.jwt;


import com.ecommerce.project.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// This annotation tells Spring to create and manage an instance of this filter.
// It makes it available for injection into other Spring components (like WebSecurityConfig).
@Component
// This filter extends OncePerRequestFilter, which guarantees that it will only be executed once
// for each incoming HTTP request, preventing redundant processing.
public class AuthTokenFilter extends OncePerRequestFilter {



    @Autowired
    private JwtUtils jwtUtils; // This tool is responsible for generating, parsing, and validating JWT tokens.

    @Autowired
    private UserDetailsServiceImpl userDetailsService;  // This service fetches user details (like roles) from your database
                                                        // based on the username extracted from the JWT.


    //Logger for console debugging
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);



    // This is the main method of the filter that gets executed for every incoming HTTP request.
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Log that the filter has been called for a specific URI, useful for tracing requests.
        logger.debug("AuthTokenFilter called for URI {}", request.getRequestURI());

        try{
            // 1. Extract the JWT from the request.
            // This method (parseJwt) will look for the JWT in the "Authorization" header.
            String jwt = parseJwt(request);

            // 2. Check if a JWT was found and if it's valid.
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                // If the JWT is valid:
                // a. Extract the username from the JWT.
                String username = jwtUtils.getUsernameFromJWTToken(jwt);
                // b. Load the full user details (including roles/authorities) from your database.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // c. Create an Authentication object.
                // This object tells Spring Security who the user is, that they are authenticated,
                // and what their authorities (roles) are. The 'null' is for credentials (password),
                // which we don't need here because the JWT already proved identity.
                UsernamePasswordAuthenticationToken  authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // d. Add additional details from the request (like IP address, session ID if any)
                // to the authentication object. This can be useful for auditing or more complex security.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // e. Set the Authentication object in Spring's SecurityContextHolder.
                // This is the most important step! It tells Spring Security that the current user
                // is now authenticated and their identity and roles are available for subsequent
                // authorization checks (e.g., hasRole("ADMIN") in WebSecurityConfig).
                SecurityContextHolder.getContext().setAuthentication(authentication);

                //logger
                logger.debug("Roles from JWT: {}", authentication.getAuthorities());

            }
        }catch (Exception e){
                logger.debug("Can not set user authentication: {}", e);
        }
                 // IMPORTANT: Pass the request and response to the next filter in the chain.
                // If this line is missing, the request will be stopped here and won't reach your controllers.
                filterChain.doFilter(request, response);

    }

    // This helper method extracts the JWT string from the HTTP request's Authorization header.
//    private String parseJwt(HttpServletRequest request) {
//        String jwt = jwtUtils.getJwtFromCookie(request);
//        logger.debug("AuthTokenFilter.java: {}", jwt);
//        return jwt;
//    }


    //for swagger
    private String parseJwt(HttpServletRequest request) {
        String jwtFromCookie = jwtUtils.getJwtFromCookie(request);
        if (jwtFromCookie != null) {
            return jwtFromCookie;
        }
        String jwtFromHeader = jwtUtils.getJwtFromHeader(request);
        if (jwtFromHeader != null) {
            return jwtFromHeader;
        }

        return null;
    }
}
