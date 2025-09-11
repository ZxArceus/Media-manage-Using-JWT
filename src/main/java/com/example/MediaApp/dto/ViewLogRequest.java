package com.example.MediaApp.dto;

import lombok.Data;

@Data
public class ViewLogRequest {
    private String mediaId;
    private String userAgent;
    private String referrer;
}
