package com.example.MediaApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreamUrlResponse {
    private String streamUrl;
    private long expiresInMinutes;
}