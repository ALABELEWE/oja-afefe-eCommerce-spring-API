package com.ecommerce.project.security.services;

import com.ecommerce.project.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserDetailImpl implements UserDetails {

    // Declares a serialVersionUID for serialization. This is good practice for Serializable classes.
    private static final long serialVersionUID = 1L;


    //dECLARE PRIVATE FIELDS TO HOLD USER DATA
    private Long id;
    private String username;
    private String email;


    // @JsonIgnore: This annotation from Jackson ensures that the 'password' field is ignored when this object is
    // serialized to JSON. This is crucial for security to prevent exposing sensitive password information in API responses.
    @JsonIgnore
    private String password;

    // A collection of authorities (roles/permissions) granted to the user.
    // The '?' wildcard with 'extends GrantedAuthority' means it can hold any type that implements GrantedAuthority.
    private Collection<? extends GrantedAuthority> authorities;

    // A constructor to initialize all fields of the UserDetailImpl object.
    // This is used when creating a UserDetailImpl instance with specific user data and authorities.
    public UserDetailImpl(Long id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }


    // A static factory method to create a UserDetailImpl object from a User entity.
    // This method is responsible for converting the application's User model into a Spring Security UserDetails object.
    public static UserDetailImpl build(User user) {

        // Converts the user's roles (from the User object) into a list of GrantedAuthority objects.
        // It streams through the roles, maps each role to a SimpleGrantedAuthority using the role's name,
        // and then collects them into a List.
        List<GrantedAuthority> authorities = user.getRoles().stream()
               .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
               .collect(Collectors.toList());

        return new UserDetailImpl(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    // Overrides the equals method from the Object class.
    // This method defines when two UserDetailImpl objects are considered equal, which is typically based on their unique identifier (id).
    // This is important for Spring Security to correctly manage user sessions and authentication.
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailImpl that = (UserDetailImpl) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(password, that.password) && Objects.equals(authorities, that.authorities);
    }

}
