package com.sportbuddy.auth;

import com.sportbuddy.enums.Gender;
import com.sportbuddy.model.Sport;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private Gender gender;
    private Set<String> sports;
    private String level;
    private Double latitude;
    private Double longitude;
    private Map<String,Object> availability; // JSON string
}
