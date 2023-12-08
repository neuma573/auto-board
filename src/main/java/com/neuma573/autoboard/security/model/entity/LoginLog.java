package com.neuma573.autoboard.security.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_log")
public class LoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id")
    private Long loginId;

    @Column(name = "login_time")
    private LocalDateTime loginTime;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "login_result")
    private String loginResult;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "session_id")
    private String sessionId;

}
