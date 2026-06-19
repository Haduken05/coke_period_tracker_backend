package com.coke.period_tracker_springboot.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Data

public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "default_cycle_length")
    private Integer defaultCycleLength = 28;

    @Column(name = "default_period_duration")
    private Integer defaultPeriodDuration = 5;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "otp_expiry_time")
    private LocalDateTime otpExpiryTime;

}
