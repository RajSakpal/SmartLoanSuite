package com.smartloansuite.user_service.service;

import com.smartloansuite.user_service.dto.LoginRequestDTO;
import com.smartloansuite.user_service.dto.LoginResponseDTO;
import com.smartloansuite.user_service.dto.UserRequestDTO;
import com.smartloansuite.user_service.dto.UserResponseDTO;
import com.smartloansuite.user_service.entity.User;
import com.smartloansuite.user_service.exception.AuthenticationException;
import com.smartloansuite.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserResponseDTO registerUser(UserRequestDTO request) {

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        User saved = userRepository.save(user);

        return UserResponseDTO.builder()
                .id(saved.getId())
                .fullName(saved.getFullName())
                .email(saved.getEmail())
                .build();
    }

    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO request) {
        if(request.getEmail()==null || request.getPassword()==null) {
            throw new IllegalArgumentException("Email and Password must be provided.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid Credentials."));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new AuthenticationException("Invalid Credentials.");
        }

        return LoginResponseDTO.builder()
                .token("d2idj2odo2dm")
                .message("Login successful.")
                .build();
    }


}
