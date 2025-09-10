package com.example.MediaApp.controller;

import com.example.MediaApp.dto.JwtResponse;
import com.example.MediaApp.dto.LoginRequest;
import com.example.MediaApp.dto.SignupRequest;
import com.example.MediaApp.model.AdminUser;

import com.example.MediaApp.repository.AdminUserRepository;
import com.example.MediaApp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        try {
            if (adminUserRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Email already exists!");
            }

            AdminUser adminUser = new AdminUser(signupRequest.getEmail(),
                    passwordEncoder.encode(signupRequest.getPassword()));
            adminUserRepository.save(adminUser);

            return ResponseEntity.ok("Admin user registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<AdminUser> userOptional = adminUserRepository.findByEmail(loginRequest.getEmail());

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found!");
            }

            AdminUser adminUser = userOptional.get();

            if (!passwordEncoder.matches(loginRequest.getPassword(), adminUser.getHashedPassword())) {
                return ResponseEntity.badRequest().body("Invalid credentials!");
            }

            String token = jwtUtil.generateToken(adminUser.getEmail());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}