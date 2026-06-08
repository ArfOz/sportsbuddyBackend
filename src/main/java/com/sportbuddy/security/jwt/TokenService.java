package com.sportbuddy.security.jwt;

import com.sportbuddy.auth.dto.AuthenticationResponse;
import com.sportbuddy.exception.TokenException;
import com.sportbuddy.model.Token;
import com.sportbuddy.model.User;
import com.sportbuddy.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenService {

    @Autowired
    private final JWTService jwtService;
    @Autowired
    private TokenRepository tokenRepository;

    public TokenService(JWTService jwtService) {
        this.jwtService = jwtService;

    }

    public AuthenticationResponse generateTokens(User user) {

        tokenRepository.revokeActiveTokenByUser(user.getId());

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken();

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

    public AuthenticationResponse refreshTokens(String refreshToken) {

        // 1) Check if the refresh token exists in the database
        Token oldToken = tokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new TokenException("Invalid refresh token"));

        // 2) Check if the token is already revoked
        if (oldToken.isRevoked()) {
            throw new TokenException("Refresh token revoked");
        }

        // 3) Check if the refresh token has expired
        if (oldToken.getRefreshExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenException("Refresh token expired");
        }

        // 4) Load the user associated with this token
        User user = oldToken.getUser();

        // 5) Generate new access and refresh tokens
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken();

        // 6) Revoke the old refresh token (keep it for history)
        oldToken.setRevoked(true);
        tokenRepository.save(oldToken);

        // 7) Create a new active token record
        Token newToken = new Token();
        newToken.setUser(user);
        newToken.setAccessToken(newAccessToken);
        newToken.setAccessExpiresAt(LocalDateTime.now().plusMinutes(15));
        newToken.setRefreshToken(newRefreshToken);
        newToken.setRefreshExpiresAt(LocalDateTime.now().plusDays(30));
        newToken.setRevoked(false);

        tokenRepository.save(newToken);

        // 8) Return the new tokens to the client
        return new AuthenticationResponse(newAccessToken, newRefreshToken);
    }


    public Token saveTokens(User user, String accessToken, String refreshToken, LocalDateTime accessExp, LocalDateTime refreshExp) {

        tokenRepository.revokeActiveTokenByUser(user.getId());

        Token token = new Token();
        token.setUser(user);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setAccessExpiresAt(accessExp);
        token.setRefreshExpiresAt(refreshExp);

        return tokenRepository.save(token);
    }

    public Token updateAccessToken(String refreshToken, String newAccessToken, LocalDateTime newAccessExp) {

        Token token = tokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new TokenException("Invalid refresh token"));

        if (token.getRefreshExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenException("Refresh token expired");
        }

        token.setAccessToken(newAccessToken);
        token.setAccessExpiresAt(newAccessExp);

        return tokenRepository.save(token);

    }

    public Token rotateTokens(String oldRefreshToken, String newAccessToken, String newRefreshToken, LocalDateTime newAccessExp, LocalDateTime newRefreshExp) {

        Token token = tokenRepository.findByRefreshToken(oldRefreshToken).orElseThrow(() -> new TokenException("Invalid refresh token"));

        if (token.getRefreshExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenException("Refresh token expired");
        }

        token.setAccessToken(newAccessToken);
        token.setAccessExpiresAt(newAccessExp);

        token.setRefreshToken(newRefreshToken);
        token.setRefreshExpiresAt(newRefreshExp);

        return tokenRepository.save(token);
    }

    public void logout(User user) {
        tokenRepository.revokeActiveTokenByUser(user.getId());
    }

}
