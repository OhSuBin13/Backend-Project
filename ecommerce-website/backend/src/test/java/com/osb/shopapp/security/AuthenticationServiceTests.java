package com.osb.shopapp.security;

import com.osb.shopapp.TestDataUtils;
import com.osb.shopapp.email.EmailService;
import com.osb.shopapp.exception.APIException;
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
import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTests {

    private static final String ACTIVATION_URL = "/activation";
    private static final String FRONTEND_BASE_URL = "/auth";
    private static final Integer REFRESH_TOKEN_EXPIRATION_TIME = 120000; // 2 minutes

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private MfaService mfaService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        // Mock external properties for fields annotated with @Value
        ReflectionTestUtils.setField(authenticationService, "frontendBaseUrl", FRONTEND_BASE_URL);
        ReflectionTestUtils.setField(authenticationService, "activationUrl", ACTIVATION_URL);
        ReflectionTestUtils.setField(authenticationService, "refreshExpirationTime", REFRESH_TOKEN_EXPIRATION_TIME);
    }

    @Test
    public void shouldRegisterUserWhenValidRequest() throws MessagingException {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .name("Test User")
                .email("test@test.com")
                .password("pass")
                .homeCountry("UK")
                .isMfaEnabled(true)
                .build();
        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .qrImageUri("uri")
                .isMfaEnabled(true)
                .build();

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role(1, "USER")));
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(mfaService.generateSecret()).thenReturn("secret");
        when(mfaService.generateQrCodeImageUri("secret", registrationRequest.getEmail())).thenReturn("uri");

        AuthenticationResponse response = authenticationService.register(registrationRequest);

        assertThat(response.getQrImageUri()).isEqualTo(expectedResponse.getQrImageUri());
        assertThat(response.getIsMfaEnabled()).isEqualTo(expectedResponse.getIsMfaEnabled());
        verify(emailService, times(1)).sendActivationEmail(
                eq(registrationRequest.getEmail()),
                eq(registrationRequest.getName()),
                startsWith(FRONTEND_BASE_URL + ACTIVATION_URL),
                any(String.class)
        );
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    public void shouldNotRegisterUserWhenUserRoleDoesNotExist() {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .name("Test User")
                .email("test@test.com")
                .password("pass")
                .homeCountry("UK")
                .isMfaEnabled(true)
                .build();

        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.register(registrationRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("USER role was not found");
    }

    @Test
    public void shouldNotRegisterUserWhenDuplicateEmail() {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .name("Test User")
                .email("test@test.com")
                .password("pass")
                .homeCountry("UK")
                .isMfaEnabled(true)
                .build();

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role(1, "USER")));
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.register(registrationRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("A user with the email '" + registrationRequest.getEmail() + "' already exists");
    }

    @Test
    public void shouldLoginUserWhenValidRequest() {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, "pass", user.getAuthorities());
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        ResponseCookie refreshCookie = ResponseCookie
                .from("refresh-token", "refreshToken")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(REFRESH_TOKEN_EXPIRATION_TIME / 1000)
                .build();
        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .accessToken("accessToken")
                .refreshTokenCookie(refreshCookie.toString())
                .isMfaEnabled(false)
                .build();

        when(authenticationManager.authenticate((any(UsernamePasswordAuthenticationToken.class))))
                .thenReturn(authenticationToken);
        when(jwtService.generateAccessToken(Map.of("name", user.getRealName()), user))
                .thenReturn("accessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");

        AuthenticationResponse response = authenticationService.login(authenticationRequest);

        assertThat(response.getAccessToken()).isEqualTo(expectedResponse.getAccessToken());
        assertThat(response.getRefreshTokenCookie()).isEqualTo(expectedResponse.getRefreshTokenCookie());
        assertThat(response.getIsMfaEnabled()).isEqualTo(expectedResponse.getIsMfaEnabled());
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    public void shouldNotLoginUserWhenMfaIsEnabled() {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));
        user.setIsMfaEnabled(true);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, "pass", user.getAuthorities());
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("test@test.com")
                .password("pass")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationToken);

        AuthenticationResponse response = authenticationService.login(authenticationRequest);

        assertThat(response.getAccessToken()).isNull();
        assertThat(response.getRefreshTokenCookie()).isNull();
        assertThat(response.getIsMfaEnabled()).isTrue();
    }

    @Test
    public void shouldActivateAccountWhenValidRequest() throws MessagingException {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));
        user.setIsEnabled(false);
        Token token = new Token(null, "code", TokenType.ACTIVATION, LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().plusMinutes(2), null, false, user);

        when(tokenRepository.findByToken("code")).thenReturn(Optional.of(token));

        authenticationService.activateAccount("code");

        assertThat(user.isEnabled()).isTrue();
        assertThat(token.getValidatedAt()).isNotNull();
    }

    @Test
    public void shouldNotActivateAccountWhenTokenHasBeenValidated() throws MessagingException {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));
        user.setIsEnabled(false);
        Token token = new Token(null, "code", TokenType.ACTIVATION, LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().plusMinutes(2), LocalDateTime.now(), false, user);

        when(tokenRepository.findByToken("code")).thenReturn(Optional.of(token));

        authenticationService.activateAccount("code");

        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    public void shouldNotActivateAccountWhenTokenHasExpired() {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));
        user.setIsEnabled(false);
        Token token = new Token(null, "code", TokenType.ACTIVATION, LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().minusMinutes(1), null, false, user);

        when(tokenRepository.findByToken("code")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authenticationService.activateAccount("code"))
                .isInstanceOf(APIException.class)
                .hasMessage("Activation code has expired. A new email has been sent");
        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    public void shouldRefreshTokenWhenValidRequest() throws IOException {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));
        Token refreshToken = new Token(null, "refreshToken", TokenType.BEARER, LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().plusMinutes(1), null, false, user);
        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .accessToken("accessToken")
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + refreshToken.getToken());
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername(refreshToken.getToken())).thenReturn(user.getEmail());
        when(userRepository.findWithAssociationsByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(tokenRepository.findByToken(refreshToken.getToken())).thenReturn(Optional.of(refreshToken));
        when(jwtService.isTokenValid(refreshToken.getToken(), user)).thenReturn(true);
        when(jwtService.generateAccessToken(Map.of("name", user.getRealName()), user)).thenReturn("accessToken");

        authenticationService.refreshToken(refreshToken.getToken(), response);

        ObjectMapper objectMapper = new ObjectMapper();
        assertThat(response.getContentAsString()).isEqualTo(objectMapper.writeValueAsString(expectedResponse));
    }

    @Test
    public void shouldNotRefreshTokenWhenMissingRefreshToken() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThatThrownBy(() -> authenticationService.refreshToken("", response))
                .isInstanceOf(JwtException.class)
                .hasMessage("No refresh token was provided");
    }

    @Test
    public void shouldNotRefreshTokenWhenUserDoesNotExist() {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));
        Token refreshToken = new Token(null, "refreshToken", TokenType.BEARER, LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().minusMinutes(1), null, false, user);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername(refreshToken.getToken())).thenReturn(user.getEmail());
        when(userRepository.findWithAssociationsByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.refreshToken(refreshToken.getToken(), response))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User with the email '" + user.getEmail() + "' does not exist");
    }

    @Test
    public void shouldNotRefreshTokenWhenRefreshTokenDoesNotExist() {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));
        Token refreshToken = new Token(null, "refreshToken", TokenType.BEARER, LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().minusMinutes(1), null, false, user);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername(refreshToken.getToken())).thenReturn(user.getEmail());
        when(userRepository.findWithAssociationsByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(tokenRepository.findByToken(refreshToken.getToken())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.refreshToken(refreshToken.getToken(), response))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Refresh token was not found");
    }

    @Test
    public void shouldNotRefreshTokenWhenRefreshTokenIsRevoked() {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));
        Token refreshToken = new Token(null, "refreshToken", TokenType.BEARER, LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().minusMinutes(1), null, true, user);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername(refreshToken.getToken())).thenReturn(user.getEmail());
        when(userRepository.findWithAssociationsByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(tokenRepository.findByToken(refreshToken.getToken())).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> authenticationService.refreshToken(refreshToken.getToken(), response))
                .isInstanceOf(JwtException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    public void shouldVerifyOtpWhenValidRequest() {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));
        user.setSecret("secret");
        user.setIsMfaEnabled(true);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, "pass", user.getAuthorities());
        VerificationRequest verificationRequest = VerificationRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .otp("otp")
                .build();
        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .accessToken("accessToken")
                .isMfaEnabled(true)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationToken);
        when(mfaService.isOtpValid(user.getSecret(), verificationRequest.getOtp())).thenReturn(true);
        when(jwtService.generateAccessToken(Map.of("name", user.getRealName()), user))
                .thenReturn("accessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");

        AuthenticationResponse response = authenticationService.verifyOtp(verificationRequest);

        assertThat(response.getAccessToken()).isEqualTo(expectedResponse.getAccessToken());
        assertThat(response.getIsMfaEnabled()).isEqualTo(expectedResponse.getIsMfaEnabled());
    }

    @Test
    public void shouldNotVerifyOtpWhenInvalidOtp() {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));
        user.setSecret("secret");
        user.setIsMfaEnabled(true);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, "pass", user.getAuthorities());
        VerificationRequest verificationRequest = VerificationRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .otp("otp")
                .build();
        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .accessToken("accessToken")
                .isMfaEnabled(true)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationToken);
        when(mfaService.isOtpValid(user.getSecret(), verificationRequest.getOtp())).thenReturn(false);

        assertThatThrownBy(() -> authenticationService.verifyOtp(verificationRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("OTP is not valid");
    }
}
