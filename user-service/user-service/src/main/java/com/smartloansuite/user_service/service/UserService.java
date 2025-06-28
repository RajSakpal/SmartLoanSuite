package com.smartloansuite.user_service.service;


import com.smartloansuite.user_service.dto.LoginRequestDTO;
import com.smartloansuite.user_service.dto.LoginResponseDTO;
import com.smartloansuite.user_service.dto.UserRequestDTO;
import com.smartloansuite.user_service.dto.UserResponseDTO;

public interface UserService {
    UserResponseDTO registerUser(UserRequestDTO request);

    LoginResponseDTO loginUser(LoginRequestDTO request);
}
