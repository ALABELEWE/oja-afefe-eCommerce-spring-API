package com.ecommerce.project.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Column(name="street")
    @Size(min=5, message = "Street name muct be atleast 5 character")
    private String street;

    @NotBlank
    @Size(min=5, message = "Building name muct be atleast 5 character")
    private String buildingName;

    @NotBlank
    @Size(min=5, message = "City name muct be atleast 5 character")
    private String city;

    @NotBlank
    @Size(min=5, message = "State name muct be atleast 5 character")
    private String state;

    @NotBlank
    @Size(min=5, message = "Country name muct be atleast 5 character")
    private String Country;

    @NotBlank
    @Size(min=5, message = "Pin code name muct be atleast 5 character")
    private String pincode;


    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    public Address(String street, String buildingName, String city, String state, String Country, String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.Country = Country;
        this.pincode = pincode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(addressId, address.addressId) && Objects.equals(street, address.street) && Objects.equals(buildingName, address.buildingName) && Objects.equals(city, address.city) && Objects.equals(state, address.state) && Objects.equals(Country, address.Country) && Objects.equals(pincode, address.pincode) && Objects.equals(user, address.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressId, street, buildingName, city, state, Country, pincode, user);
    }


}
