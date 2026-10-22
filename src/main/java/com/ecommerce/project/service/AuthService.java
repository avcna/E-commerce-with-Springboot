package com.ecommerce.project.service;

import com.ecommerce.project.payload.UserResponse;
import org.springframework.data.domain.Pageable;

public interface AuthService {
    UserResponse getAllSellers(Pageable pageDetail);
}
