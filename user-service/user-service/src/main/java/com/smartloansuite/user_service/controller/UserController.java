package com.smartloansuite.user_service.controller;

import com.smartloansuite.user_service.dto.LoginRequestDTO;
import com.smartloansuite.user_service.dto.LoginResponseDTO;
import com.smartloansuite.user_service.dto.UserRequestDTO;
import com.smartloansuite.user_service.dto.UserResponseDTO;
import com.smartloansuite.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register (@Validated @RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login (@Validated @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }

}
