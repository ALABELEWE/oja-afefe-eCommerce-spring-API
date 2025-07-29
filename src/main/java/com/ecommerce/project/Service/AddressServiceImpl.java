package com.ecommerce.project.Service;


import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repository.AddressRepository;
import com.ecommerce.project.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO,Address.class);
        List<Address> addressList = user.getAddresses();

       boolean isAddressNotPresent = true;

        for(Address address1 : addressList) {
            if(address1.getPincode().equals(addressDTO.getPincode()) || addressDTO.getStreet().equals(address1.getStreet())
            || addressDTO.getCity().equals(address1.getCity() ) || addressDTO.getState().equals(address1.getState())
            || addressDTO.getCountry().equals(address1.getCountry()) || addressDTO.getBuildingName().equals(address1.getBuildingName())) {
                isAddressNotPresent = false;
                break;
            }
        }


        if(isAddressNotPresent) {
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(savedAddress,AddressDTO.class);
        }else {
            throw new APIException("Product already exists");
        }
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream()
                .map(address->modelMapper.map(address,AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO findAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address" ,"Address Id",addressId));
        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public  List<AddressDTO> getUserAddresses(User user){
        List<Address> addresses = user.getAddresses();
        return addresses.stream()
                .map(address->modelMapper.map(address,AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address" ,"Address Id",addressId));

        addressFromDatabase.setCity(addressDTO.getCity());
        addressFromDatabase.setCountry(addressDTO.getCountry());
        addressFromDatabase.setStreet(addressDTO.getStreet());
        addressFromDatabase.setBuildingName(addressDTO.getBuildingName());
        addressFromDatabase.setState(addressDTO.getState());
        addressFromDatabase.setPincode(addressDTO.getPincode());

        Address updatedAddress = addressRepository.save(addressFromDatabase);

        User userFromDatabase = addressFromDatabase.getUser();
        userFromDatabase.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userFromDatabase.getAddresses().add(updatedAddress);
        userRepository.save(userFromDatabase);
        return modelMapper.map(updatedAddress,AddressDTO.class);
    }

    @Override
    @Transactional
    public String deleteAddress(Long addressId) {
//        // 1. Find the address
//        Address address = addressRepository.findById(addressId)
//                .orElseThrow(()-> new ResourceNotFoundException("Address" ,"Address Id",addressId));
//
//        // 2. Remove address from the associated user's address collection
//        User userFromDatabase = address.getUser();
//        userFromDatabase.getAddresses().removeIf(address1 ->  address1.getAdressId().equals(addressId));
//        userRepository.save(userFromDatabase);
//
//        addressRepository.delete(address);
//        return "Address with " + addressId + " was deleted successfully";

        // 1. Find the address
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address" ,"Address Id",addressId));

        // 2. Get the associated user
        User userFromDatabase = address.getUser();

        if (userFromDatabase != null) {
            // Remove the address from the user's collection
            // This will trigger orphanRemoval=true when the transaction commits
            // because the 'Address' object will no longer be referenced by its 'User' parent.
            // The crucial part here is that Address.equals() and hashCode() MUST be correct
            // for remove() to correctly identify and remove the object from the List.
            userFromDatabase.getAddresses().remove(address); // Use 'remove(object)' directly
            userRepository.save(userFromDatabase); // Explicitly save the user to ensure changes are flushed
        } else {
            // If an address somehow exists without a user (e.g., nullable user_id FK)
            // then you'd still need to explicitly delete it.
            addressRepository.delete(address);
        }

        return "Address with " + addressId + " was deleted successfully";
    }
}
