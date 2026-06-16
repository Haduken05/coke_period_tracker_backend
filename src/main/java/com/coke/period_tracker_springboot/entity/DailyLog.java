package com.coke.period_tracker_springboot.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "daily_logs")
@Data

public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "flow_intensity")
    private Integer flowIntensity = 0;

    @Column(name = "cramp_severity")
    private Integer crampSeverity = 0;

    @Column(name = "mood")
    private String mood;

    @Column(name = "symptoms")
    private String symptoms;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
