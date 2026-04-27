package com.osb.shopapp.token;

import com.osb.shopapp.TestDataUtils;
import com.osb.shopapp.role.Role;
import com.osb.shopapp.role.RoleRepository;
import com.osb.shopapp.user.User;
import com.osb.shopapp.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class TokenRepositoryTest {

    @Container
    @ServiceConnection
    private static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.33");

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private User user;
    private Token activationToken;
    private Token validRefreshToken;
    private Token invalidRefreshToken;

    @Autowired
    public TokenRepositoryTest(TokenRepository tokenRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @BeforeEach
    public void setUp() {
        Role adminRole = new Role(null, "ADMIN");
        adminRole = roleRepository.save(adminRole);
        user = TestDataUtils.createUserA(Set.of(adminRole));
        user.setId(null);
        user = userRepository.save(user);

        activationToken = new Token(null, "activationCode", TokenType.ACTIVATION, LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().plusMinutes(2), null, false, user);
        activationToken = tokenRepository.save(activationToken);
        validRefreshToken = new Token(null, "validRefreshToken", TokenType.BEARER, LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().plusMinutes(2), null, false, user);
        validRefreshToken = tokenRepository.save(validRefreshToken);
        invalidRefreshToken = new Token(null, "invalidRefreshToken", TokenType.BEARER, LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().plusMinutes(2), null, true, user);
        invalidRefreshToken = tokenRepository.save(invalidRefreshToken);
    }

    @Test
    public void shouldFindActivationTokenByUserEmail() {
        Optional<Token> token = tokenRepository.findActivationTokenByUserEmail(user.getEmail());
        assertThat(token.isPresent()).isTrue();
        assertThat(token.get().getTokenType()).isEqualTo(TokenType.ACTIVATION);
        assertThat(token.get().getToken()).isEqualTo(activationToken.getToken());
    }

    @Test
    public void shouldFindAllValidRefreshTokenByUserEmail() {
        List<Token> tokens = tokenRepository.findAllValidRefreshTokensByUserEmail(user.getEmail());

        assertThat(tokens.size()).isEqualTo(1);
        assertThat(tokens.get(0).getTokenType()).isEqualTo(TokenType.BEARER);
        assertThat(tokens.get(0).getToken()).isEqualTo(validRefreshToken.getToken());
        assertThat(tokens.get(0).getIsRevoked()).isFalse();
    }
}
