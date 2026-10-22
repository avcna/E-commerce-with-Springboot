package com.ecommerce.project.service;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.UserDTO;
import com.ecommerce.project.payload.UserResponse;
import com.ecommerce.project.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public UserResponse getAllSellers(Pageable pageable) {
        Page<User> allUser = userRepository.findByRoleName(AppRole.ROLE_SELLER, pageable);
        List<UserDTO> userDTOS = allUser.getContent().stream().map(p -> modelMapper.map(p, UserDTO.class)).toList();
        UserResponse userResponse = new UserResponse();
        userResponse.setContent(userDTOS);
        userResponse.setPageNumber(allUser.getNumber());
        userResponse.setPageSize(allUser.getSize());
        userResponse.setTotalPages(allUser.getTotalPages());
        userResponse.setTotalElements(allUser.getTotalElements());
        userResponse.setLastpage(userResponse.isLastpage());

        return userResponse;
    }
}
