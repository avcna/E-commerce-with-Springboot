package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import com.ecommerce.project.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO getAddressesById(Long id) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new APIException("No address was found with id " + id));
        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressesByUser(User user) {
        List<Address> addresses = user.getAddresses();
        return addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO updateAddresses(Long id, AddressDTO addressDTO) {
        Address addressFromDb = addressRepository.findById(id).orElseThrow(() -> new APIException("No address was found with id " + id));
        addressFromDb.setCity(addressDTO.getCity());
        addressFromDb.setCountry(addressDTO.getCountry());
        addressFromDb.setStreet(addressDTO.getStreet());
        addressFromDb.setBuildingName(addressDTO.getBuildingName());
        addressFromDb.setPincode(addressDTO.getPincode());
        addressFromDb.setState(addressDTO.getState());

        Address updatedAddress = addressRepository.save(addressFromDb);

        User userFromDb = addressFromDb.getUser();
        userFromDb.getAddresses().removeIf(address -> address.getAddressId().equals(id));
        userFromDb.getAddresses().add(updatedAddress);
        userRepository.save(userFromDb);

        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddresses(Long id) {
        Address addressFromDb = addressRepository.findById(id).orElseThrow(() -> new APIException("No address was found with id " + id));

        User userFromDb = addressFromDb.getUser();
        userFromDb.getAddresses().removeIf(address -> address.getAddressId().equals(id));
        userRepository.save(userFromDb);

        addressRepository.delete(addressFromDb);

        return "Successfully deleted address with id " + id;
    }
}
