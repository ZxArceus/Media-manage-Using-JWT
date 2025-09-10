package com.example.MediaApp.dto;

import lombok.Data;

@Data
public class MediaRequest {
    private String title;
    private String type; // video/audio
    private String fileUrl;
}