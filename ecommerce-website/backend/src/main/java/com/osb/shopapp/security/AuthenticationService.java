package com.osb.shopapp.security;

import com.osb.shopapp.email.EmailService;
import com.osb.shopapp.exception.ResourceAlreadyExistsException;
import com.osb.shopapp.exception.ResourceNotFoundException;
import com.osb.shopapp.role.Role;
import com.osb.shopapp.role.RoleRepository;
import com.osb.shopapp.token.JwtService;
import com.osb.shopapp.token.Token;
import com.osb.shopapp.token.TokenRepository;
import com.osb.shopapp.token.TokenType;
import com.osb.shopapp.user.User;
import com.osb.shopapp.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${application.frontend.base-url")
    private String frontendBaseUrl;

    @Value("{application.mailing.activation-url}")
    private String activationUrl;

    @Value("{application.security.jwt.refresh-token.expiration}")
    private long refreshExpirationTime;

    @Transactional
    public AuthenticationResponse register(RegistrationRequest registrationRequest) throws MessagingException {
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("USER role was not found"));
        if (userRepository.existsByEmail(registrationRequest.getEmail()))
            throw new ResourceAlreadyExistsException(
                    "A user with the email '" + registrationRequest.getEmail() + "' already exists"
            );

        User user = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(Set.of(userRole))
                .homeCountry(registrationRequest.getHomeCountry())
                .registeredAt(LocalDate.now())
                .isEnabled(false)
                .isMfaEnabled(registrationRequest.getIsMfaEnabled())
                .build();

        // Generate secret and QR code image if MFA is enabled
        String qrImageUri = null;
        if (registrationRequest.getIsMfaEnabled()) {
            return null;
        }

        userRepository.save(user);
        sendActivationEmail(user);

        return AuthenticationResponse.builder()
                .qrImageUri(qrImageUri)
                .isMfaEnabled(registrationRequest.getIsMfaEnabled())
                .build();
    }

    private void sendActivationEmail(User user) throws MessagingException {
        String activationCode = generateAndSaveActivationCode(user);

        emailService.sendActivationEmail(
                user.getEmail(),
                user.getName(),
                frontendBaseUrl + activationUrl + activationCode,
                activationCode
        );
    }

    private String generateAndSaveActivationCode(User user) {
        String generatedCode = generateActivationCode(6);
        tokenRepository.save(Token.builder()
                .token(generatedCode)
                .tokenType(TokenType.ACTIVATION)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .isRevoked(false)
                .user(user)
                .build()
        );

        return generatedCode;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIdx = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIdx));
        }

        return codeBuilder.toString();
    }

    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );
        User user = (User) authentication.getPrincipal();

        //Client needs to send OTP
        if (user.getIsMfaEnabled()) {
            return AuthenticationResponse.builder()
                    .isMfaEnabled(true)
                    .build();
        }

        return generateToken(user);
    }

    private AuthenticationResponse generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getRealName());
        String accessToken = jwtService.generateAccessToken(claims, user);
        String refreshToken = jwtService.generateRefreshToken(user);
        tokenRepository.save(Token.builder()
                .token(refreshToken)
                .tokenType(TokenType.BEARER)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plus(refreshExpirationTime, ChronoUnit.MILLIS))
                .isRevoked(false)
                .user(user)
                .build()
        );

        boolean inProduction = "prod".equals(activeProfile);

        // Include refresh token in HttpOnly cookie
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie
                .from("refresh-token", refreshToken)
                .httpOnly(true)
                .secure(inProduction)
                .path("/")
                .maxAge(refreshExpirationTime / 1000);
        if (inProduction) {
            cookieBuilder.sameSite("None")
                    .domain(".resellmart.tmeras.com");
        }

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshTokenCookie(cookieBuilder.build().toString())
                .isMfaEnabled(user.getIsMfaEnabled())
                .build();
    }
}
