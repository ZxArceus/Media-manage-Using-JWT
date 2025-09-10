package com.example.MediaApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "media_view_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaViewLog {
    @Id
    private String id;

    private String mediaId;

    private String viewedByIp;

    private LocalDateTime timestamp;

    public MediaViewLog(String mediaId, String viewedByIp) {
        this.mediaId = mediaId;
        this.viewedByIp = viewedByIp;
        this.timestamp = LocalDateTime.now();
    }
}