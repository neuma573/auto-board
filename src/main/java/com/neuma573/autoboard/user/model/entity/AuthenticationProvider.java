package com.neuma573.autoboard.user.model.entity;

import com.neuma573.autoboard.user.model.enums.AuthenticationProviderType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "authentication_provider")
public class AuthenticationProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String providerId;

    @Enumerated(EnumType.STRING)
    private AuthenticationProviderType provider;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
