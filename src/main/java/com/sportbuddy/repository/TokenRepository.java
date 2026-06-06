package com.sportbuddy.repository;

import com.sportbuddy.model.Token;
import com.sportbuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface TokenRepository extends JpaRepository<Token, Long> {

    // Used during refresh to find the token record
    Optional<Token> findByRefreshToken(String refreshToken);

    // Used if you want to validate access tokens from DB
    Optional<Token> findByAccessToken(String accessToken);

    // Fetch full token history for a user
    List<Token> findAllByUser(User user);

    // Fetch the currently active token (revoked = false)
    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.revoked = false")
    Optional<Token> findActiveTokenByUser(Long userId);

    // Revoke only the active token (used during login)

    @Modifying
    @Query("UPDATE Token t SET t.revoked = true WHERE t.user.id = :userId AND t.revoked = false")
    void revokeActiveTokenByUser(Long userId);
}
