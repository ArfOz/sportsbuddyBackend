package com.sportbuddy.repository;

import com.sportbuddy.model.Token;
import com.sportbuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByAccessToken(String accessToken);

    Optional<Token> findByRefreshToken(String refreshToken);

    Optional<Token> findByUser(User user);

    void deleteByUser(User user);

    void deleteByAccessToken(String accessToken);
}
