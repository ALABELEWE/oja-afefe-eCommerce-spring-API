package com.ecommerce.project.Service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;


public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAddresses();

    AddressDTO findAddressById(Long addressId);

    List<AddressDTO> getUserAddresses(User user);

    @Transactional
    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

    @Transactional
    String deleteAddress(Long addressId);
}
