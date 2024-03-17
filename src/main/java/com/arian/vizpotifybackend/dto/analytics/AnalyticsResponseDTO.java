package com.arian.vizpotifybackend.dto.analytics;

// write a record class for the response of the analytics endpoint with isProcessing
public record AnalyticsResponseDTO(
       AnalyticsDTO analyticsData,
       boolean isProcessing
) {
}