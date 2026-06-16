package com.coke.period_tracker_springboot.controller;

import com.coke.period_tracker_springboot.entity.DailyLog;
import com.coke.period_tracker_springboot.entity.UserProfile;
import com.coke.period_tracker_springboot.service.PeriodService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/period")
@CrossOrigin(origins = "*")
public class PeriodController {

    @Autowired
    private PeriodService periodService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getOrCreateProfile(){
        UserProfile profile = periodService.getOrCreateDefaultProfile();
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/log")
    public ResponseEntity<DailyLog> logDailyMetrics(
            @RequestParam Long userId,
            @RequestBody DailyLog incomingLog){

        if(incomingLog.getLogDate() == null){
            incomingLog.setLogDate(LocalDate.now());
        }

        DailyLog savedLog = periodService.saveDailyLog(userId, incomingLog);
        return ResponseEntity.ok(savedLog);
    }

    @GetMapping("/logs")
    public ResponseEntity<List<DailyLog>> getUserLogs(@RequestParam Long userId){
        List<DailyLog> logs = periodService.getUserLogs(userId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/prediction")
    public ResponseEntity<LocalDate> getNextPeriodPrediction(@RequestParam Long userId){
        LocalDate predictedDate = periodService.predictNextPeriodStartDate(userId);
        return ResponseEntity.ok(predictedDate);
    }
}
