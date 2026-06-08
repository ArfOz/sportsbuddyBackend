package com.sportbuddy.controller;


import com.sportbuddy.auth.AuthService;
import com.sportbuddy.auth.dto.AuthenticationResponse;
import com.sportbuddy.auth.dto.LoginRequest;
import com.sportbuddy.auth.dto.RefreshRequest;
import com.sportbuddy.auth.dto.RegisterRequest;
import com.sportbuddy.dto.response.ApiResponse;
import com.sportbuddy.model.User;
import com.sportbuddy.security.annotations.Public;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;


    @Public
    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody RegisterRequest registerRequest) {

        User savedUser = authService.register(registerRequest);
        return ApiResponse.<User>builder().success(true).data(savedUser).message("User registered successfully").build();
    }

    @Public
    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        AuthenticationResponse auth = authService.login(loginRequest);

        return ApiResponse.<AuthenticationResponse>builder().success(true).data(auth).message("Login successful").build();
    }


    @Public
    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        AuthenticationResponse auth = authService.refreshToken(request.getRefreshToken());
        return ApiResponse.<AuthenticationResponse>builder().success(true).data(auth).message("User registered successfully").build();
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@Valid Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        authService.logout(user);

        return ApiResponse.<String>builder().success(true).message("Logged out successfully").build();
    }

}
