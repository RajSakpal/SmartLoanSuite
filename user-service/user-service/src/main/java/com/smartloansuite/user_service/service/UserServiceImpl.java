package com.smartloansuite.user_service.service;

import com.smartloansuite.user_service.dto.LoginRequestDTO;
import com.smartloansuite.user_service.dto.LoginResponseDTO;
import com.smartloansuite.user_service.dto.UserRequestDTO;
import com.smartloansuite.user_service.dto.UserResponseDTO;
import com.smartloansuite.user_service.entity.Role;
import com.smartloansuite.user_service.entity.User;
import com.smartloansuite.user_service.entity.enums.RoleName;
import com.smartloansuite.user_service.exception.AuthenticationException;
import com.smartloansuite.user_service.repository.RoleRepository;
import com.smartloansuite.user_service.repository.UserRepository;
import com.smartloansuite.user_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtUtil jwtUtil;

    @Override
    public UserResponseDTO registerUser(UserRequestDTO request) {

        // Fetch role from DB
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        String verificationCode = UUID.randomUUID().toString();

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .isEmailVerified(false)
                .verificationCode(verificationCode)
                .build();

        User saved = userRepository.save(user);

        // Send verification email
        emailService.sendVerificationEmail(saved.getEmail(), saved.getVerificationCode());

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
                .token(jwtUtil.generateToken(request.getEmail()))
                .message("Login successful.")
                .build();
    }


}
