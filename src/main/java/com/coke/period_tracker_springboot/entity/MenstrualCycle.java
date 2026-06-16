package com.coke.period_tracker_springboot.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "menstrual_cycles")
@Data

public class MenstrualCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cycle_id")
    private Long cycleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "cycle_length")
    private Integer cycleLength;

    @Column(name = "period_duration")
    private Integer periodDuration;

    @Column(name = "is_anomaly")
    private Boolean isAnomaly = false;

}
