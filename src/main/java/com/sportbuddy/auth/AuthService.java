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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AuthService(
            UserRepository userRepository, TokenRepository tokenRepository, JWTService jwtService, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;

    }

    public AuthenticationResponse register(RegisterRequest request) {
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
        String refreshToken = jwtService.generateRefreshToken();

        return new AuthenticationResponse(accessToken, refreshToken);
    }


    public AuthenticationResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Revoke only the active token (keep history)
        tokenRepository.revokeActiveTokenByUser(user.getId());

        // Generate new tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken();

        // Create new token record (active)
        Token token = new Token();
        token.setUser(user);
        token.setAccessToken(accessToken);
        token.setAccessExpiresAt(LocalDateTime.now().plusMinutes(15));
        token.setRefreshToken(refreshToken);
        token.setRefreshExpiresAt(LocalDateTime.now().plusDays(30));
        token.setRevoked(false);

        tokenRepository.save(token);

        return new AuthenticationResponse(accessToken, refreshToken);
    }


    public AuthenticationResponse refreshToken(String refreshToken) {

        // Find the token record by refresh token
        Token oldToken = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // Check if already revoked
        if (oldToken.isRevoked()) {
            throw new RuntimeException("Refresh token revoked");
        }

        // Check expiration
        if (oldToken.getRefreshExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        // Load user from token record
        User user = oldToken.getUser();

        // Generate new tokens
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken();

        // Revoke the old token (keep it as history)
        oldToken.setRevoked(true);
        tokenRepository.save(oldToken);

        // Create new active token record
        Token newToken = new Token();
        newToken.setUser(user);
        newToken.setAccessToken(newAccessToken);
        newToken.setAccessExpiresAt(LocalDateTime.now().plusMinutes(15));
        newToken.setRefreshToken(newRefreshToken);
        newToken.setRefreshExpiresAt(LocalDateTime.now().plusDays(30));
        newToken.setRevoked(false);

        tokenRepository.save(newToken);

        return new AuthenticationResponse(newAccessToken, newRefreshToken);
    }


    public void logout(String accessToken){
        Token token = tokenRepository.findByAccessToken(accessToken).orElseThrow(()-> new RuntimeException("Invalid access token"));

        token.setRevoked(true);

        token.setRefreshExpiresAt(LocalDateTime.now());

        tokenRepository.save(token);
    }


}
