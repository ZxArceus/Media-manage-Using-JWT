package com.example.MediaApp.controller;

import com.example.MediaApp.dto.MediaRequest;
import com.example.MediaApp.dto.StreamUrlResponse;
import com.example.MediaApp.service.MediaService;
import com.example.MediaApp.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;
    @Autowired
    private JwtUtil jwtUtil;
    @PostMapping
    public ResponseEntity<?> addMedia(@RequestBody MediaRequest mediaRequest) {
        try {
            String mediaId = mediaService.addMedia(mediaRequest);
            return ResponseEntity.ok("Media added successfully with ID: " + mediaId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/stream-url")
    public ResponseEntity<?> getStreamUrl(@PathVariable String id, HttpServletRequest request) {
        try {
            String clientIp = getClientIp(request);

            StreamUrlResponse streamUrl = mediaService.generateStreamUrl(id, clientIp);
            return ResponseEntity.ok(streamUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/stream/{id}") public ResponseEntity<?> streamMedia(@PathVariable String id, @RequestParam String token, @RequestParam long expires) {
        try { // Check if current time is past expires time
            if (System.currentTimeMillis() > expires) {
                return ResponseEntity.status(401).body("Stream URL has expired"); }
            // Validate JWT token
            if (!jwtUtil.validateStreamToken(token, id)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }
         return ResponseEntity.ok("Stream access granted for media: " + id); }
         catch (Exception e) {
            return ResponseEntity.status(401).body("Token validation failed: " + e.getMessage()); } }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
