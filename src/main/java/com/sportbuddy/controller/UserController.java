package com.sportbuddy.controller;


import com.sportbuddy.dto.response.ApiResponse;
import com.sportbuddy.model.User;
import com.sportbuddy.security.annotations.Public;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {


    @GetMapping("/about")
    public ApiResponse<User> about(@AuthenticationPrincipal User user) {


        return ApiResponse.<User>builder()
                .success(true)
                .data(user)
                .message("User profile fetched successfully")
                .build();
    }

}
