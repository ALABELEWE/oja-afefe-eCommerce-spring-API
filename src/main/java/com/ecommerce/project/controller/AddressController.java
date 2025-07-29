package com.ecommerce.project.controller;

import com.ecommerce.project.Service.AddressService;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class AddressController {


    @Autowired
    private AuthUtil authUtil;

    private AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }



    @Tag(name="Address APIs", description = "API for managing all address")
    @Operation(summary = "Create Address", description = "API to Create a new address")
    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress( @Valid @RequestBody AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);
        return new ResponseEntity<>(savedAddressDTO,HttpStatus.CREATED);
    }

    @Tag(name="Address APIs", description = "API for managing all address")
    @Operation(summary = "Get Address", description = "API to get address")
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAddress() {
        List<AddressDTO> listOfAddresses = addressService.getAddresses();
        return new ResponseEntity<>(listOfAddresses,HttpStatus.OK);
    }

    @Tag(name="Address APIs", description = "API for managing all address")
    @Operation(summary = "Get Address", description = "API to get address with ID")
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable Long addressId) {
        AddressDTO addressDTO = addressService.findAddressById(addressId);
        return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }

    @Tag(name="Address APIs", description = "API for managing all address")
    @Operation(summary = "Create Addresses", description = "API to get address by user")
    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses() {
        User user = authUtil.loggedInUser();
        List<AddressDTO> listOfAddresses = addressService.getUserAddresses(user);
        return new ResponseEntity<>(listOfAddresses,HttpStatus.OK);
    }

    @Tag(name="Address APIs", description = "API for managing all address")
    @Operation(summary = "Edit Address", description = "API to edit address using address Id")
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long addressId,
    @RequestBody AddressDTO addressDTO){
        AddressDTO updatedAddress = addressService.updateAddress(addressId, addressDTO);
        return new ResponseEntity<>(updatedAddress,HttpStatus.OK);
    }

    @Tag(name="Address APIs", description = "API for managing all address")
    @Operation(summary = "Delete Address", description = "API to delete address using ID")
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }


}
