package com.arian.vizpotifybackend.comparison;

import com.arian.vizpotifybackend.analytics.core.AnalyticsDto;
import com.arian.vizpotifybackend.artist.ArtistDto;
import com.arian.vizpotifybackend.track.TrackDto;
import com.arian.vizpotifybackend.user.topitems.artist.UserTopArtistService;
import com.arian.vizpotifybackend.user.topitems.track.UserTopTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComparisonService {

    private final UserTopArtistService userTopArtistService;
    private final UserTopTrackService userTopTrackService;

    public ComparisonDto compareUsers(String userId1, String userId2) {
//        AnalyticsDto analytics1 = analyticsService.getAnalyticsForUser(userId1);
//        AnalyticsDto analytics2 = analyticsService.getAnalyticsForUser(userId2);
//        Map<String, List<ArtistDto>> topArtists1 = userTopArtistService.getUserTopArtists(userId1);
//        Map<String, List<ArtistDto>> topArtists2 = userTopArtistService.getUserTopArtists(userId2);
//        Map<String, List<TrackDto>> topTracks1 = userTopTrackService.getUserTopItems(userId1);
//        Map<String, List<TrackDto>> topTracks2 = userTopTrackService.getUserTopItems(userId2);
//
//        Map<String, Double> jaccardSimilarity = calculateJaccardSimilarity(topArtists1, topArtists2, topTracks1, topTracks2);
//        CommonItemsDto commonItems = findCommonItems(topArtists1, topArtists2, topTracks1, topTracks2);
//
//        return new ComparisonDto(
//                commonItems,
//                jaccardSimilarity,
//                Map.of(userId1, topTracks1, userId2, topTracks2),
//                Map.of(userId1, analytics1.userMusicEraSummary(), userId2, analytics2.userMusicEraSummary())
////                Map.of(userId1, analytics1.userArtistTrackCount(), userId2, analytics2.userArtistTrackCount()),
////                Map.of(userId1, analytics1.userGenreDistribution(), userId2, analytics2.userGenreDistribution()),
////                Map.of(userId1, analytics1.userTrackFeatureStats(), userId2, analytics2.userTrackFeatureStats())
//        );
        return null;
    }

    private Map<String, Double> calculateJaccardSimilarity(
            Map<String, List<ArtistDto>> topArtists1,
            Map<String, List<ArtistDto>> topArtists2,
            Map<String, List<TrackDto>> topTracks1,
            Map<String, List<TrackDto>> topTracks2) {
        Map<String, Double> similarity = new HashMap<>();

        for (String timeRange : topArtists1.keySet()) {
            Set<String> artists1 = topArtists1.get(timeRange).stream().map(ArtistDto::getId).collect(Collectors.toSet());
            Set<String> artists2 = topArtists2.get(timeRange).stream().map(ArtistDto::getId).collect(Collectors.toSet());
            similarity.put("artists_" + timeRange, calculateJaccard(artists1, artists2));

            Set<String> tracks1 = topTracks1.get(timeRange).stream().map(TrackDto::getId).collect(Collectors.toSet());
            Set<String> tracks2 = topTracks2.get(timeRange).stream().map(TrackDto::getId).collect(Collectors.toSet());
            similarity.put("tracks_" + timeRange, calculateJaccard(tracks1, tracks2));
        }

        return similarity;
    }

    private double calculateJaccard(Set<String> set1, Set<String> set2) {
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return (double) intersection.size() / union.size();
    }

    private CommonItemsDto findCommonItems(
            Map<String, List<ArtistDto>> topArtists1,
            Map<String, List<ArtistDto>> topArtists2,
            Map<String, List<TrackDto>> topTracks1,
            Map<String, List<TrackDto>> topTracks2) {
        Map<String, String> commonArtists = new HashMap<>();
        Map<String, Map<String, String>> commonTracks = new HashMap<>();

        for (String timeRange : topArtists1.keySet()) {
            Set<String> artists1 = topArtists1.get(timeRange).stream().map(ArtistDto::getId).collect(Collectors.toSet());
            Set<String> artists2 = topArtists2.get(timeRange).stream().map(ArtistDto::getId).collect(Collectors.toSet());
            Set<String> commonArtistIds = new HashSet<>(artists1);
            commonArtistIds.retainAll(artists2);

            for (String artistId : commonArtistIds) {
                ArtistDto artist = topArtists1.get(timeRange).stream()
                        .filter(a -> a.getId().equals(artistId))
                        .findFirst()
                        .orElseThrow();
                commonArtists.put(artistId, artist.getName());
            }

            Set<String> tracks1 = topTracks1.get(timeRange).stream().map(TrackDto::getId).collect(Collectors.toSet());
            Set<String> tracks2 = topTracks2.get(timeRange).stream().map(TrackDto::getId).collect(Collectors.toSet());
            Set<String> commonTrackIds = new HashSet<>(tracks1);
            commonTrackIds.retainAll(tracks2);

            Map<String, String> commonTracksForTimeRange = new HashMap<>();
            for (String trackId : commonTrackIds) {
                TrackDto track = topTracks1.get(timeRange).stream()
                        .filter(t -> t.getId().equals(trackId))
                        .findFirst()
                        .orElseThrow();
                commonTracksForTimeRange.put(trackId, track.getName());
            }
            commonTracks.put(timeRange, commonTracksForTimeRange);
        }

        return new CommonItemsDto(commonArtists, commonTracks);
    }
}
