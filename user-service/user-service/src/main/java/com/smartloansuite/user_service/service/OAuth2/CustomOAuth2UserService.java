package com.smartloansuite.user_service.service.OAuth2;

import com.smartloansuite.user_service.entity.User;
import com.smartloansuite.user_service.repository.UserRepository;
import com.smartloansuite.user_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            var delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            String username = oAuth2User.getAttribute("username");

            // Check if user exists
            Optional<User> existingUserOpt = userRepository.findByEmail(email);

            User user = existingUserOpt.orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setUsername(email);
                newUser.setFullName(name);
                newUser.setRole("USER");
                newUser.setPassword("N/A");
                newUser.setEmailVerified(true); // since Google already verifies
                return userRepository.save(newUser);
            });

            // Add the JWT to authorities
            String token = jwtUtil.generateToken(user.getUsername());

            Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
            attributes.put("jwt", token);

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "email"
            );
        }catch (Exception ex) {
            log.error("OAuth2 processing failed", ex);
            throw new OAuth2AuthenticationException(new OAuth2Error("server_error", ex.getMessage(), null), ex.getMessage(), ex);
        }
    }
}

