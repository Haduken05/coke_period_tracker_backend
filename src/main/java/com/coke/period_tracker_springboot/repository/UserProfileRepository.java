package com.coke.period_tracker_springboot.repository;

import com.coke.period_tracker_springboot.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}
