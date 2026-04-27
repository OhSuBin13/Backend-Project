package com.osb.shopapp.security;

import com.osb.shopapp.TestDataUtils;
import com.osb.shopapp.role.Role;
import com.osb.shopapp.token.JwtService;
import com.osb.shopapp.token.Token;
import com.osb.shopapp.token.TokenRepository;
import com.osb.shopapp.token.TokenType;
import com.osb.shopapp.user.User;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LogoutServiceTests {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    public void shouldLogoutUserWhenValidRequest() {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));
        String accessToken = "accessToken";
        Token refreshToken = new Token(null, "refreshToken", TokenType.BEARER, LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now().plusMinutes(1), null, false, user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities()
        );

        when(jwtService.extractUsername(accessToken)).thenReturn(user.getEmail());
        when(tokenRepository.findAllValidRefreshTokensByUserEmail(user.getEmail())).thenReturn(List.of(refreshToken));

        logoutService.logout(request, response, authentication);

        assertThat(refreshToken.getIsRevoked()).isTrue();
    }

    @Test
    public void shouldNotLogoutUserWhenMissingAccessToken() throws UnsupportedEncodingException {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities()
        );
        String expectedResponse = "{\"error\": \"No access token in Bearer header\"}";

        logoutService.logout(request, response, authentication);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }

    @Test
    public void shouldNotLogoutUserWhenInvalidAccessToken() throws UnsupportedEncodingException {
        User user = TestDataUtils.createUserA(Set.of(new Role(1, "USER")));
        String accessToken = "accessToken";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities()
        );
        String expectedResponse = "{\"error\": \"Invalid access token\"}";

        when(jwtService.extractUsername(accessToken)).thenThrow(new JwtException("Invalid access token"));

        logoutService.logout(request, response, authentication);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).isEqualTo(expectedResponse);
    }
}
