package com.arian.vizpotifybackend.analytics.util;

import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStats;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsDto;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnalyticsUtility {

    public static <T, R> Map<String, R> groupByTimeRange(
            List<T> input,
            Function<T, String> timeRangeExtractor,
            Function<T, R> mapper
    ) {
        return input.stream()
                .collect(Collectors.toMap(
                        timeRangeExtractor,
                        mapper,
                        (existing, replacement) -> existing
                ));
    }

}
