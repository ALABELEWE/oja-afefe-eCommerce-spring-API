package com.ecommerce.project.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    // A logger to print messages to the console, for debugging.....
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    // This is the core method that gets called when an unauthenticated user tries to access a protected resource
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // Log the reason for the authentication failure. This is very helpful for debugging!
        logger.debug("Authentication required: {}", authException.getMessage());

        // Set the content type of the response to JSON. This tells the client (e.g., Postman)
        // that the response body will be in JSON format.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Set the HTTP status code to 401 (Unauthorized). This is the standard status code
        // for requests that require authentication but it was not provided or was invalid.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Create a Map to hold the error details that we want to send back as JSON.
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());


        // Create an ObjectMapper, which is a tool from the Jackson library.
        // It's used to convert Java objects (our 'body' Map) into JSON strings.
        final ObjectMapper mapper = new ObjectMapper();

        // Write the JSON representation of our 'body' Map directly to the response's output stream.
        // This sends the JSON error message back to the client.
        mapper.writeValue(response.getOutputStream(), body);
    }
}
