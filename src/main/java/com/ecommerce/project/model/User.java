package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @Column(name="username")
    @NotBlank
    @Size(max=15)
    private String userName;

    @NotBlank
    @Size(max=50)
    @Column(name="email")
    private String email;

    @NotBlank
    @Size(max=200)
    @Column(name="password")
    private String password;


    @ManyToMany(cascade= {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH},
            fetch=FetchType.EAGER)
    @JoinTable(name="user_role",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = {CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval=true)
    private List<Address> addresses = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "user",
            cascade= {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private Set<Product> products;

    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade= {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval=true)
    private Cart cart;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

}
