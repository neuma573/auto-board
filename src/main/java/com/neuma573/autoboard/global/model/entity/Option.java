package com.neuma573.autoboard.global.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "app_option")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String optionKey;

    @Getter
    @Setter
    @Column(columnDefinition = "TEXT", name = "\"value\"")
    private String value;
}
