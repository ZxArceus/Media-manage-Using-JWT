package com.example.MediaApp.repository;


import com.example.MediaApp.model.AdminUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface AdminUserRepository extends MongoRepository<AdminUser, String> {
    Optional<AdminUser> findByEmail(String email);
}