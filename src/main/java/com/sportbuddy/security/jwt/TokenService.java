package com.sportbuddy.security.jwt;

import com.sportbuddy.model.Token;
import com.sportbuddy.model.User;
import com.sportbuddy.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public Token saveTokens(User user, String accessToken, String refreshToken,
                            LocalDateTime accessExp, LocalDateTime refreshExp) {

        tokenRepository.deleteByUser(user);

        Token token = new Token();
        token.setUser(user);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setAccessExpiresAt(accessExp);
        token.setRefreshExpiresAt(refreshExp);

        return tokenRepository.save(token);
    }

    public Token updateAccessToken(String refreshToken, String newAccessToken, LocalDateTime newAccessExp){

        Token token = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.getRefreshExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        token.setAccessToken(newAccessToken);
        token.setAccessExpiresAt(newAccessExp);

        return tokenRepository.save(token);

    }

    public Token rotateTokens(String oldRefreshToken,
                              String newAccessToken,
                              String newRefreshToken,
                              LocalDateTime newAccessExp,
                              LocalDateTime newRefreshExp) {

        Token token = tokenRepository.findByRefreshToken(oldRefreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.getRefreshExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        token.setAccessToken(newAccessToken);
        token.setAccessExpiresAt(newAccessExp);

        token.setRefreshToken(newRefreshToken);
        token.setRefreshExpiresAt(newRefreshExp);

        return tokenRepository.save(token);
    }

    public void logout(User user) {
        tokenRepository.deleteByUser(user);
    }

}
