package com.smartloansuite.user_service.controller;

import com.smartloansuite.user_service.entity.User;
import com.smartloansuite.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("code") String code) {
        Optional<User> userOpt = userRepository.findByVerificationCode(code);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid verification code");
        }

        User user = userOpt.get();
        user.setEmailVerified(true);
        user.setVerificationCode(null); // clear it
        userRepository.save(user);

        return ResponseEntity.ok("Email verified successfully! 🎉 You can now log in.");
    }
}
