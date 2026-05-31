package com.sportbuddy.auth;

import com.sportbuddy.enums.Gender;
import com.sportbuddy.model.Sport;
import lombok.Data;

import java.util.List;

@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private Gender gender;
    private List<Sport> sports;
    private String level;
    private Double latitude;
    private Double longitude;
    private String availability; // JSON string
}
