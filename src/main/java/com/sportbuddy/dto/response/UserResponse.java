package com.sportbuddy.dto.response;

import com.sportbuddy.enums.Gender;
import com.sportbuddy.model.Role;
import com.sportbuddy.model.Sport;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Gender gender;
    private String level;
    private Double latitude;
    private Double longitude;
    private Map<String, Object> availability;
    private Set<Sport> sports;
    private Set<Role> roles;
    private LocalDateTime createdAt;
}
