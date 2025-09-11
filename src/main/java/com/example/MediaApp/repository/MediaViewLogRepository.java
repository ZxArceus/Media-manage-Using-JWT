package com.example.MediaApp.repository;


import com.example.MediaApp.model.MediaViewLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaViewLogRepository extends MongoRepository<MediaViewLog, String> {
}