package com.osb.shopapp.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Email is not in a valid format")
    private String email;

    @NotBlank(message = "Password must not be empty")
    private String password;
}
