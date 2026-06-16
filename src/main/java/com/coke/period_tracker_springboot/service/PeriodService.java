package com.coke.period_tracker_springboot.service;

import com.coke.period_tracker_springboot.entity.DailyLog;
import com.coke.period_tracker_springboot.entity.UserProfile;

import com.coke.period_tracker_springboot.repository.DailyLogRepository;
import com.coke.period_tracker_springboot.repository.MenstrualCycleRepository;
import com.coke.period_tracker_springboot.repository.UserProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PeriodService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private DailyLogRepository dailyLogRepository;

    @Autowired
    private MenstrualCycleRepository menstrualCycleRepository;

    public UserProfile getOrCreateDefaultProfile(){
        return userProfileRepository.findById(1L).orElseGet(() -> {
            UserProfile defaultProfile = new UserProfile();
            return userProfileRepository.save(defaultProfile);
        });
    }

    public DailyLog saveDailyLog(Long userId, DailyLog incomingLog){
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Profile Not Found!"));

        return dailyLogRepository.findByUserProfile_UserIdAndLogDate(userId, incomingLog.getLogDate())
                .map(existingLog -> {
                    // Update existing log
                    existingLog.setFlowIntensity(incomingLog.getFlowIntensity());
                    existingLog.setCrampSeverity(incomingLog.getCrampSeverity());
                    existingLog.setMood(incomingLog.getMood());
                    existingLog.setSymptoms(incomingLog.getSymptoms());
                    existingLog.setNotes(incomingLog.getNotes());
                    return dailyLogRepository.save(existingLog);
                })
                .orElseGet(() -> {
                    // Link the new log to the user profile and save it
                    incomingLog.setUserProfile(profile);
                    return dailyLogRepository.save(incomingLog);
                });
    }

    public List<DailyLog> getUserLogs(Long userId){
        return dailyLogRepository.findByUserProfile_UserId(userId);
    }

    public LocalDate predictNextPeriodStartDate(Long userId){
        // Fetch Past Cycles calculated in the Database (descending)
        List<com.coke.period_tracker_springboot.entity.MenstrualCycle> cycles =
                menstrualCycleRepository.findByUserProfile_UserIdOrderByStartDateDesc(userId);

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Profile Not Found!"));

        int cycleLengthToUse = profile.getDefaultCycleLength(); // fallback default (28)

        // If the user has tracked history, calculate their real rolling average instead of the default
        if(!cycles.isEmpty()){
            int totalDays = 0;
            int validCyclesCount = 0;

            for(com.coke.period_tracker_springboot.entity.MenstrualCycle cycle : cycles){
                // Ignore anomalies to keep predictions accurate
                if(cycle.getCycleLength() != null && !cycle.getIsAnomaly()){
                    totalDays += cycle.getCycleLength();
                    validCyclesCount++;
                }
            }

            if(validCyclesCount > 0){
                cycleLengthToUse = totalDays / validCyclesCount;
            }
        }

        if(!cycles.isEmpty()){
            LocalDate lastPeriodStart = cycles.get(0).getStartDate();
            return lastPeriodStart.plusDays(cycleLengthToUse);
        }

        return LocalDate.now();
    }

}
