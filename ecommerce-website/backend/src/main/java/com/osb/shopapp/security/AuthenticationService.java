package com.osb.shopapp.security;

import com.osb.shopapp.token.JwtService;
import com.osb.shopapp.token.TokenRepository;
import com.osb.shopapp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
}
