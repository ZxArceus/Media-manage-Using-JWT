package com.example.MediaApp.service;

import com.example.MediaApp.dto.MediaRequest;
import com.example.MediaApp.dto.StreamUrlResponse;
import com.example.MediaApp.model.MediaAsset;
import com.example.MediaApp.model.MediaViewLog;
import com.example.MediaApp.repository.MediaAssetRepository;
import com.example.MediaApp.repository.MediaViewLogRepository;
import com.example.MediaApp.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MediaService {

    @Autowired
    private MediaAssetRepository mediaAssetRepository;

    @Autowired
    private MediaViewLogRepository mediaViewLogRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public String addMedia(MediaRequest mediaRequest) {
        // Validate media type
        if (!mediaRequest.getType().equals("video") && !mediaRequest.getType().equals("audio")) {
            throw new IllegalArgumentException("Media type must be 'video' or 'audio'");
        }

        MediaAsset mediaAsset = new MediaAsset(
                mediaRequest.getTitle(),
                mediaRequest.getType(),
                mediaRequest.getFileUrl()
        );

        MediaAsset saved = mediaAssetRepository.save(mediaAsset);
        return saved.getId();
    }

    public StreamUrlResponse generateStreamUrl(String mediaId, String clientIp) {
        // Check if media exists
        Optional<MediaAsset> mediaOptional = mediaAssetRepository.findById(mediaId);
        if (mediaOptional.isEmpty()) {
            throw new IllegalArgumentException("Media not found");
        }

        MediaAsset media = mediaOptional.get();

        // Log the view
        MediaViewLog viewLog = new MediaViewLog(mediaId, clientIp);
        mediaViewLogRepository.save(viewLog);

        // Generate secure stream URL with 10-minute expiration
        String streamToken = jwtUtil.generateStreamToken(mediaId);
        String secureStreamUrl = generateSecureUrl(media.getFileUrl(), streamToken);

        return new StreamUrlResponse(secureStreamUrl, 10);
    }

    private String generateSecureUrl(String originalUrl, String token) {
        // In a real application, you would generate a signed URL with expiration
        // For this demo, we're appending a token parameter
        return originalUrl + "?token=" + token + "&expires=" + (System.currentTimeMillis() + 600000); // 10 minutes
    }
}
