package com.example.MediaApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "admin_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUser {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String hashedPassword;

    private LocalDateTime createdAt;

    public AdminUser(String email, String hashedPassword) {
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.createdAt = LocalDateTime.now();
    }
}