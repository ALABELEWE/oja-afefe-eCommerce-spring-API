package com.ecommerce.project.security;


import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.RoleRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.security.jwt.AuthEntryPointJwt;
import com.ecommerce.project.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- The Main Security Rules (FilterChain) ---
    // This is the core of your security rules – it's like setting up all the gates and checkpoints.

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF (Cross-Site Request Forgery) protection is disabled here.
                // For APIs that use JWTs, CSRF is often disabled because JWTs
                // are typically sent in headers, not cookies, reducing CSRF risk.
                // However, be aware of the implications.
                .csrf(csrf -> csrf.disable())

                // If an unauthenticated user tries to access a protected resource,
                // our 'unauthorizedHandler' (the bouncer) will step in.
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))

                // This is crucial for JWT! It tells Spring Security: "Don't create or use
                // traditional HTTP sessions." Each request should carry its own JWT token
                // for authentication, making the server "stateless."
                .sessionManagement(Session ->
                        Session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))


                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/auth/**").permitAll()  // Anyone can access login/signup (auth) endpoints.
                                .requestMatchers("/h2-console/**").permitAll()  // Allow access to H2 database console (useful for development).
                                .requestMatchers("/swagger-ui/**").permitAll()  //  Allow access to Swagger UI for API documentation.
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/api/test/**").permitAll()  // Allow access to test endpoints
                                .requestMatchers("/images/**").permitAll()    // Allow access to image files
                                .anyRequest().authenticated());  // ALL OTHER requests need to be authenticated (user must be logged in).

        // Tell Spring to use our custom authentication provider.
        http.authenticationProvider(authenticationProvider());

        // This line is important! It inserts our JWT token checking filter
        // *before* Spring's default username/password filter. This means JWTs
        // are checked first for authentication.
        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);

        // This helps prevent clickjacking attacks, especially important for H2 console.
        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()
        ));

        // Build and return our complete security rule set.
        return http.build();
    }


    // This is like saying: "For these specific paths, don't even bother with the security filters."
    // It's often used for static resources or API documentation paths that don't need any security checks.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }


    // --- Initial Data Setup (CommandLineRunner) ---
    // This is a special Spring Boot feature. It runs code *once* when your application starts up.
    // Here, it's used to make sure you have some basic roles (USER, SELLER, ADMIN) and
    // some initial users (user1, seller1, admin) in your database. This is super handy
    // for development so you don't have to manually add them every time.

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Retrieve or create roles
            Role userRole = roleRepository.findByRole(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRole.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            // Retrieve or create roles:
            // It tries to find each role (USER, SELLER, ADMIN).
            // If a role doesn't exist, it creates a new Role object and saves it to the database.
            Role sellerRole = roleRepository.findByRole(AppRole.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRole.ROLE_SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            Role adminRole = roleRepository.findByRole(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            // Define sets of roles for different user types
            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole); // Admin gets all roles



            // Create users if not already present:
            // It checks if users "user1", "seller1", and "admin" exist.
            // If not, it creates new User objects with encoded passwords and saves them.
            if (!userRepository.existsByUserName("user1")) {
                User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
                userRepository.save(user1);
            }

            if (!userRepository.existsByUserName("seller1")) {
                User seller1 = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
                userRepository.save(seller1);
            }

            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
                userRepository.save(admin);
            }

            // Update roles for existing users
            // The following `ifPresent` blocks are redundant if roles are set during creation.
            // They would only be needed if you were updating roles for users that *already* existed
            // in the database from a previous run without roles.
            // For a fresh start with CommandLineRunner, setting roles during initial user creation is sufficient.
            // For example, if you ran the app once, then added the setRoles lines to the 'if' blocks above,
            // these 'ifPresent' blocks would ensure existing users get their roles updated.
            userRepository.findByUserName("user1").ifPresent(user -> {
                user.setRoles(userRoles);
                userRepository.save(user);
            });

            userRepository.findByUserName("seller1").ifPresent(seller -> {
                seller.setRoles(sellerRoles);
                userRepository.save(seller);
            });

            userRepository.findByUserName("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            });
        };
    }
}
