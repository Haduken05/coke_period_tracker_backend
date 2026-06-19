package com.coke.period_tracker_springboot.controller;

import com.coke.period_tracker_springboot.entity.UserProfile;
import com.coke.period_tracker_springboot.repository.UserProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserProfile incomingProfile){

        if(userProfileRepository.findByEmail(incomingProfile.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body(Map.of("error", "Email is already registered!"));
        }

        String securedPassword = passwordEncoder.encode(incomingProfile.getPassword());
        incomingProfile.setPassword(securedPassword);

        UserProfile savedUser = userProfileRepository.save(incomingProfile);
        return ResponseEntity.ok(Map.of(
                "message", "Registration Successful!",
                "userId", savedUser.getUserId()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginCredentials){
        String email = loginCredentials.get("email");
        String rawPassword = loginCredentials.get("password");

        Optional<UserProfile> userOpt = userProfileRepository.findByEmail(email);

        if(userOpt.isPresent()){
            UserProfile user = userOpt.get();

            if(passwordEncoder.matches(rawPassword, user.getPassword())){

                return ResponseEntity.ok(Map.of(
                        "message", "Login Successful!",
                        "token", "mock-jwt-token-xyz-123",
                        "userId", user.getUserId(),
                        "email", user.getEmail()
                ));
            }
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password!"));
    }
}
