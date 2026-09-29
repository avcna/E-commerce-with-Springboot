package com.ecommerce.project.service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAddresses();

    AddressDTO getAddressesById(Long id);

    List<AddressDTO> getAddressesByUser(User user);

    AddressDTO updateAddresses(Long id, AddressDTO addressDTO);

    String deleteAddresses(Long id);
}
