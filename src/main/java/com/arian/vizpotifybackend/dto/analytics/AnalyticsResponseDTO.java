package com.arian.vizpotifybackend.dto.analytics;

public record AnalyticsResponseDTO(
       AnalyticsDTO analyticsData,
       boolean isProcessing
) {
}