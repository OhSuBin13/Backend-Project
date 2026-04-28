package com.osb.shopapp.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.osb.shopapp.role.Role;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Integer id;

    private String name;

    private String email;

    private String homeCountry;

    private LocalDate registeredAt;

    private Boolean isEnabled;

    private Boolean isMfaEnabled;

    private byte[] profileImage;

    private Set<Role> roles;

    private String qrImageUri;
}
