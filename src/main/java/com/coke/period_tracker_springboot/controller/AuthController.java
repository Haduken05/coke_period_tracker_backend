package com.coke.period_tracker_springboot.controller;

import com.coke.period_tracker_springboot.entity.UserProfile;
import com.coke.period_tracker_springboot.repository.UserProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @Autowired
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request){
        String email = request.get("email");

        Optional<UserProfile> existingUserOpt = userProfileRepository.findByEmail(email);

        if(existingUserOpt.isPresent()){
            UserProfile user = existingUserOpt.get();
            if(user.getPassword() != null && !"PENDING_VERIFICATION".equalsIgnoreCase(user.getPassword())){
                return ResponseEntity.badRequest().body(Map.of("error", "Email is already registered"));
            }
        }

        String otpCode = String.valueOf((int)((Math.random() * 900000) + 100000));

        UserProfile profile = existingUserOpt.orElse(new UserProfile());
        profile.setEmail(email);
        profile.setVerificationCode(otpCode);
        profile.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));

        if (profile.getPassword() == null) {
            profile.setPassword("PENDING_VERIFICATION");
        }

        userProfileRepository.save(profile);

        org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Account Verification Code");
        message.setText("Welcome! Use this registration code to complete your verification account setup: " + otpCode);

        mailSender.send(message);

        return ResponseEntity.ok(Map.of("message", "Verification code has been sent to your email address"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registrationPayload){
        String email = registrationPayload.get("email");
        String rawPassword = registrationPayload.get("password");
        String submittedCode = registrationPayload.get("code");

        Optional<UserProfile> profileOpt = userProfileRepository.findByEmail(email);
        if(profileOpt.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("error", "No verification session found. Request a code first."));
        }

        UserProfile profile = profileOpt.get();

        if(profile.getOtpExpiryTime() == null || profile.getOtpExpiryTime().isBefore(LocalDateTime.now())){
            return ResponseEntity.badRequest().body(Map.of("error", "Verification code has expired! Please request a new one."));
        }

        if(profile.getVerificationCode() == null || !profile.getVerificationCode().equals(submittedCode)){
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid verification code!"));
        }

        profile.setVerificationCode(null);
        profile.setOtpExpiryTime(null);

        profile.setPassword(passwordEncoder.encode(rawPassword));

        UserProfile savedUser = userProfileRepository.save(profile);
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

            if("PENDING_VERIFICATION".equals(user.getPassword())){
                return ResponseEntity.status(401).body(Map.of("error", "Please complete your email registration setup first!"));
            }

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