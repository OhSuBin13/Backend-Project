package com.osb.shopapp.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String homeCountry;

    private LocalDate registeredAt;

    private String imagePath;

    private Boolean isEnabled;

    private Boolean isMfaEnabled;

    private Boolean isDeleted;

    private String secret;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public String getRealName() {
        return this.name;
    }
}
