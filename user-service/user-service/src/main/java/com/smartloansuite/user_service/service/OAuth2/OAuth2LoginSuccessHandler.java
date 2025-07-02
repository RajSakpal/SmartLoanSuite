package com.smartloansuite.user_service.service.OAuth2;

import com.smartloansuite.user_service.entity.User;
import com.smartloansuite.user_service.repository.UserRepository;
import com.smartloansuite.user_service.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = token.getPrincipal();
        String email = user.getAttribute("email");

        User dbUser = userRepository.findByEmail(email).orElseThrow(); // should exist at this point

        String jwt = jwtUtil.generateToken(dbUser.getUsername());

        ResponseCookie cookie = ResponseCookie.from("token", jwt)
                .httpOnly(true)
                .secure(false) // set true if HTTPS
                .path("/")
                .maxAge(3600)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Redirect to frontend success page
        response.sendRedirect("http://localhost:3000/oauth-success");
    }
}

