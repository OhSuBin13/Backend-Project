package com.osb.shopapp.token;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<@NonNull Token, @NonNull Integer> {

    Optional<Token> findByToken(String token);

    boolean existsByToken(String token);

    @Query("""
            SELECT t FROM Token t WHERE t.tokenType = 'ACTIVATION'
            AND t.user.email = :email
            """)
    Optional<Token> findActivationTokenByUserEmail(String email);

    @Query("""
            SELECT t FROM Token t WHERE t.isRevoked = false
            AND t.tokenType = 'BEARER AND t.user.email = :email
            """)
    List<Token> findAllValidRefreshTokensByUserEmail(String email);
}
