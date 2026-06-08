package com.sportbuddy.auth;

import com.sportbuddy.auth.dto.AuthenticationResponse;
import com.sportbuddy.auth.dto.LoginRequest;
import com.sportbuddy.auth.dto.RegisterRequest;
import com.sportbuddy.enums.RoleName;
import com.sportbuddy.exception.BusinessException;
import com.sportbuddy.model.Role;
import com.sportbuddy.model.Sport;
import com.sportbuddy.model.User;
import com.sportbuddy.repository.RoleRepository;
import com.sportbuddy.repository.SportRepository;
import com.sportbuddy.repository.UserRepository;
import com.sportbuddy.security.jwt.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final SportRepository sportRepository;
    private final TokenService tokenService;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, SportRepository sportRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.sportRepository = sportRepository;
        this.tokenService = tokenService;

    }

    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use");
        }


        User user = new User();


        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLevel(request.getLevel());
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());
        user.setAvailability(request.getAvailability());

        Set<Sport> sportEntities = request.getSports().stream().map(name -> sportRepository.findByName(name.toUpperCase()).orElseThrow(() -> new BusinessException("Sport not found: " + name))).collect(Collectors.toSet());

        user.setSports(sportEntities);

        Role userRole = roleRepository.findByName(RoleName.USER).orElseThrow(() -> new BusinessException("Role not found"));

        user.getRoles().add(userRole);

        return userRepository.save(user);
    }


    public AuthenticationResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BusinessException("Email not found"));


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid password");
        }

        return tokenService.generateTokens(user);

    }


    public AuthenticationResponse refreshToken(String refreshToken) {
        return tokenService.refreshTokens(refreshToken);
    }


    public void logout(User user) {
        tokenService.logout(user);
    }


}
