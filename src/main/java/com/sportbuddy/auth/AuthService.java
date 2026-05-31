package com.sportbuddy.auth;

import com.sportbuddy.enums.RoleName;
import com.sportbuddy.model.Role;
import com.sportbuddy.model.Token;
import com.sportbuddy.model.User;
import com.sportbuddy.repository.RoleRepository;
import com.sportbuddy.repository.TokenRepository;
import com.sportbuddy.repository.UserRepository;
import com.sportbuddy.security.jwt.JWTService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AuthService(
            UserRepository userRepository, TokenRepository tokenRepository, JWTService jwtService, PasswordEncoder passwordEncoder, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;

    }

    public AuthenticationResponse register(RegisterRequest request){
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setGender(request.getGender());
        user.setSports(request.getSports());
        user.setLevel(request.getLevel());
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());
        user.setAvailability(request.getAvailability());

        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return new AuthenticationResponse(accessToken, refreshToken);
    }


    public AuthenticationResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()->new RuntimeException("User not found"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public AuthenticationResponse refreshToken(String refreshToken){
        Token refreshEntity= tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        User user = refreshEntity.getUser();

        String newAccessToken = jwtService.generateToken(user);

        String newRefreshToken =jwtService.generateRefreshToken();


    }


}
