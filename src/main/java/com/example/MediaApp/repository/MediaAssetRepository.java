package com.example.MediaApp.repository;

import com.example.MediaApp.model.MediaAsset;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MediaAssetRepository extends MongoRepository<MediaAsset, String> {
}