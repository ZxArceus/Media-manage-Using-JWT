package com.example.MediaApp.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaAnalyticsResponse {
    private long totalViews;
    private long uniqueIps;
    private Map<String, Long> viewsPerDay;

    private String mostActiveDay;
    private double averageViewsPerDay;
}
