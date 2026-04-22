package com.osb.shopapp.token;

import com.osb.shopapp.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private LocalDateTime validatedAt;

    private Boolean isRevoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;
}
