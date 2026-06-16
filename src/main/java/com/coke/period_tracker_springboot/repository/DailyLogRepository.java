package com.coke.period_tracker_springboot.repository;

import com.coke.period_tracker_springboot.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {

    List<DailyLog> findByUserProfile_UserId(Long userId);

    Optional<DailyLog> findByUserProfile_UserIdAndLogDate(Long userId, LocalDate logDate);
}
