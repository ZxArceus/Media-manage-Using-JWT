package com.example.MediaApp.controller;

import com.example.MediaApp.model.User;
import com.example.MediaApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        // Hide passwords
        users.forEach(user -> user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()))
        );
        return ResponseEntity.ok(users);
    }
}