package com.coke.period_tracker_springboot.repository;

import com.coke.period_tracker_springboot.entity.MenstrualCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenstrualCycleRepository extends JpaRepository<MenstrualCycle, Long> {

    List<MenstrualCycle> findByUserProfile_UserIdOrderByStartDateDesc(Long userId);
}
