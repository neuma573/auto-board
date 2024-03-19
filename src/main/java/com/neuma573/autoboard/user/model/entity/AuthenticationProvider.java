package com.neuma573.autoboard.user.model.entity;

import com.neuma573.autoboard.user.model.enums.AuthenticationProviderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "authentication_provider")
public class AuthenticationProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private AuthenticationProviderType provider;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
