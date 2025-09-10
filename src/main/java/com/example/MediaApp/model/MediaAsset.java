package com.example.MediaApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "media_assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaAsset {
    @Id
    private String id;

    private String title;

    private String type; // video/audio

    private String fileUrl;

    private LocalDateTime createdAt;

    public MediaAsset(String title, String type, String fileUrl) {
        this.title = title;
        this.type = type;
        this.fileUrl = fileUrl;
        this.createdAt = LocalDateTime.now();
    }
}
