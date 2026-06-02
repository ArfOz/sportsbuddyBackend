package com.sportbuddy.controller;


import com.sportbuddy.auth.AuthService;
import com.sportbuddy.auth.AuthenticationResponse;
import com.sportbuddy.auth.LoginRequest;
import com.sportbuddy.auth.RegisterRequest;
import com.sportbuddy.dto.response.ApiResponse;
import com.sportbuddy.model.User;
import com.sportbuddy.security.annotations.Public;
import lombok.RequiredArgsConstructor;
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
    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody LoginRequest loginRequest) {

        AuthenticationResponse auth = authService.login(loginRequest);

        return ApiResponse.<AuthenticationResponse>builder()
                .success(true)
                .data(auth)
                .message("Login successful")
                .build();
    }

    @Public
    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody RegisterRequest registerRequest) {

        User savedUser = authService.register(registerRequest);
        return ApiResponse.<User>builder()
                .success(true)
                .data(savedUser)
                .message("User registered successfully")
                .build();
    }
}
