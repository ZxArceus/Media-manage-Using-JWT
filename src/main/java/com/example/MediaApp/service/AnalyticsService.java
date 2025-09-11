package com.example.MediaApp.service;

import com.example.MediaApp.dto.MediaAnalyticsResponse;
import com.example.MediaApp.model.MediaAsset;
import com.example.MediaApp.model.MediaViewLog;
import com.example.MediaApp.repository.MediaAssetRepository;
import com.example.MediaApp.repository.MediaViewLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class AnalyticsService {
    @Autowired private MediaViewLogRepository mediaViewLogRepository;
    @Autowired private MediaAssetRepository mediaAssetRepository;
    @Autowired private MongoTemplate mongoTemplate;


    public void logView(String mediaId, String clientIp, String userAgent, String referrer) {
    // Verify media exists
    Optional<MediaAsset> media = mediaAssetRepository.findById(mediaId);
    if (media.isEmpty())
        { throw new IllegalArgumentException("Media not found with ID: " + mediaId); }

    // Create enhanced view log with all tracking data
     MediaViewLog viewLog = new MediaViewLog(mediaId, clientIp, userAgent, referrer);

    mediaViewLogRepository.save(viewLog);
}
public MediaAnalyticsResponse getMediaAnalytics(String mediaId) {
    // Verify media exists
    Optional<MediaAsset> media = mediaAssetRepository.findById(mediaId);
    if (media.isEmpty())
    { throw new IllegalArgumentException("Media not found with ID: " + mediaId); }
    // Get all view logs for this specific media
    Query query = new Query(Criteria.where("mediaId").is(mediaId));
    List<MediaViewLog> viewLogs = mongoTemplate.find(query, MediaViewLog.class);
    if (viewLogs.isEmpty())
        { return new MediaAnalyticsResponse(0, 0, new HashMap<>(), "No data", 0.0); }

    // Calculate total views
    long totalViews = viewLogs.size();
    // Calculate unique IPs
    long uniqueIps = viewLogs.stream() .map(MediaViewLog::getViewedByIp) .distinct() .count();
    // Group views by day
    Map<String, Long> viewsPerDay = viewLogs.stream()
                    .collect(Collectors.groupingBy( log -> log.getTimestamp()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE), Collectors.counting() ));

    // Find most active day
    String mostActiveDay = viewsPerDay.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("No data");
    // Calculate average views per day
    double averageViewsPerDay = viewsPerDay.isEmpty() ? 0.0 : viewsPerDay.values()
            .stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);
    return new MediaAnalyticsResponse( totalViews, uniqueIps, viewsPerDay,  mostActiveDay, averageViewsPerDay ); }
    public DashboardResponse getDashboardAnalytics() {
    // Count total media assets
        long totalMediaAssets = mediaAssetRepository.count();
        // Count total views
        long totalViews = mediaViewLogRepository.count();
        // Count unique viewers (distinct IPs)
        List<String> allIps = mongoTemplate.findDistinct("viewedByIp", MediaViewLog.class, String.class);
        long uniqueViewers = allIps.size();
        // Get views from last 7 days
        LocalDateTime sevenDaysAgo = LocalDateTime.now()
                .minusDays(7);
        Query recentQuery = new Query(Criteria.where("timestamp")
                .gte(sevenDaysAgo));

        List<MediaViewLog> recentViews = mongoTemplate.find(recentQuery, MediaViewLog.class);

        Map<String, Long> viewsLast7Days = recentViews.stream()
                .collect(Collectors.groupingBy( log -> log.getTimestamp()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE), Collectors.counting() ));
        // Get top viewed media
        List<DashboardResponse.TopMediaItem> topViewedMedia = getTopViewedMedia(5);
        // Get media type distribution
        List<MediaAsset> allMedia = mediaAssetRepository.findAll();
        Map<String, Long> mediaTypeDistribution = allMedia.stream()
                .collect(Collectors.groupingBy( MediaAsset::getType, Collectors.counting() ));

        return new DashboardResponse( totalMediaAssets, totalViews, uniqueViewers, viewsLast7Days, topViewedMedia, mediaTypeDistribution );

    }
    private List<DashboardResponse.TopMediaItem> getTopViewedMedia(int limit) {

    // Use MongoDB aggregation to get top viewed media
        Aggregation aggregation = Aggregation.newAggregation( Aggregation.group("mediaId")
                .count().as("viewCount"),
                Aggregation.sort(org.springframework.data.domain.Sort.by("viewCount").descending()), Aggregation.limit(limit) );

        AggregationResults<Map> results = mongoTemplate.aggregate( aggregation, "mediaview_logs", Map.class);

        List<DashboardResponse.TopMediaItem> topMedia = new ArrayList<>();
        for (Map result : results.getMappedResults())
        {
            String mediaId = (String) result.get("id");
            Long viewCount = ((Number) result.get("viewCount")).
                    longValue();

            Optional<MediaAsset> media = mediaAssetRepository.findById(mediaId);
            if (media.isPresent())
            {
                MediaAsset asset = media.get();
                topMedia.add(new DashboardResponse.TopMediaItem( mediaId, asset.getTitle(), asset.getType(), viewCount ));
            }
        }
        return topMedia;
    }

    public Map<String, Object> getAdvancedAnalytics(String mediaId, String period) {
    // Verify media exists
        Optional<MediaAsset> media = mediaAssetRepository.findById(mediaId);
        if (media.isEmpty())
        {
            throw new IllegalArgumentException("Media not found with ID: " + mediaId);
        }
        // Determine date range based on period
        LocalDateTime startDate;
        switch (period.toLowerCase())
        {
            case "week": startDate = LocalDateTime.now().minusDays(7);
            break;
            case "month": startDate = LocalDateTime.now().minusDays(30);
            break;
            case "year": startDate = LocalDateTime.now().minusDays(365);
            break;
            default: startDate = LocalDateTime.now().minusDays(30);
        }
        Query query = new Query( Criteria.where("mediaId").is(mediaId)
                .and("timestamp")
                .gte(startDate) );

        List<MediaViewLog> viewLogs = mongoTemplate.find(query, MediaViewLog.class);

        Map<String, Object> analytics = new HashMap<>();
        // Hourly view distribution
        Map<Integer, Long> hourlyViews = viewLogs.stream()
                .collect(Collectors.groupingBy( log -> log.getTimestamp().getHour(), Collectors.counting() ));

        // Weekly view distribution
        Map<String, Long> weeklyViews = viewLogs.stream()
                .collect(Collectors.groupingBy( log -> log.getTimestamp().getDayOfWeek().toString(), Collectors.counting() ));

        // Referrer analysis
        Map<String, Long> referrerStats = viewLogs.stream()
                .filter(log -> log.getReferrer() != null && !log.getReferrer().isEmpty())
                .collect(Collectors.groupingBy( MediaViewLog::getReferrer, Collectors.counting() ));

        // User retention analysis
        Map<String, Long> userRetention = viewLogs.stream()
                .collect(Collectors.groupingBy( MediaViewLog::getViewedByIp, Collectors.counting() ))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .collect(Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ));

        analytics.put("hourlyDistribution", hourlyViews);
        analytics.put("weeklyDistribution", weeklyViews);
        analytics.put("referrerStats", referrerStats);
        analytics.put("returningUsers", userRetention.size());
        analytics.put("totalReturningViews", userRetention.values()
                .stream()
                .mapToLong(Long::longValue).sum());

        analytics.put("period", period);
        analytics.put("startDate", startDate);
        return analytics;
            }
        }

