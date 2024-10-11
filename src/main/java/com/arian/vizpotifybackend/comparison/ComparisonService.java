package com.arian.vizpotifybackend.comparison;

import com.arian.vizpotifybackend.analytics.artist.UserArtistTrackCountMapDto;
import com.arian.vizpotifybackend.analytics.artist.UserArtistTrackCountService;
import com.arian.vizpotifybackend.analytics.era.UserMusicEraSummaryMapDto;
import com.arian.vizpotifybackend.analytics.era.UserMusicEraSummaryService;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsMapDto;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsService;
import com.arian.vizpotifybackend.analytics.genre.UserGenreDistributionMapDto;
import com.arian.vizpotifybackend.analytics.genre.UserGenreDistributionService;
import com.arian.vizpotifybackend.artist.ArtistDto;
import com.arian.vizpotifybackend.track.TrackDto;
import com.arian.vizpotifybackend.user.topitems.artist.UserTopArtistService;
import com.arian.vizpotifybackend.user.topitems.track.UserTopTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComparisonService {

    private final UserTopArtistService userTopArtistService;
    private final UserTopTrackService userTopTrackService;
    private final UserMusicEraSummaryService userMusicEraSummaryService;
    private final UserTrackFeatureStatsService userTrackFeatureStatsService;
    private final UserGenreDistributionService userGenreDistributionService;
    private final UserArtistTrackCountService userArtistTrackCountService;

    public ComparisonDto compareUsers(String userId1, String userId2) {
        Map<String, List<ArtistDto>> topArtists1 = userTopArtistService.getUserTopArtists(userId1);
        Map<String, List<ArtistDto>> topArtists2 = userTopArtistService.getUserTopArtists(userId2);
        Map<String, List<TrackDto>> topTracks1 = userTopTrackService.getUserTopItems(userId1);
        Map<String, List<TrackDto>> topTracks2 = userTopTrackService.getUserTopItems(userId2);

        Map<String, Double> jaccardSimilarity = calculateJaccardSimilarity(topArtists1, topArtists2, topTracks1, topTracks2);
        CommonItemsDto commonItems = findCommonItems(topArtists1, topArtists2, topTracks1, topTracks2);

        UserMusicEraSummaryMapDto eraSummary1 = userMusicEraSummaryService.fetchUserMusicEraSummary(userId1);
        UserMusicEraSummaryMapDto eraSummary2 = userMusicEraSummaryService.fetchUserMusicEraSummary(userId2);

        UserTrackFeatureStatsMapDto featureStats1 = userTrackFeatureStatsService.fetchUserTrackFeatureStats(userId1);
        UserTrackFeatureStatsMapDto featureStats2 = userTrackFeatureStatsService.fetchUserTrackFeatureStats(userId2);

        UserGenreDistributionMapDto genreDistribution1 = userGenreDistributionService.fetchUserGenreDistribution(userId1);
        UserGenreDistributionMapDto genreDistribution2 = userGenreDistributionService.fetchUserGenreDistribution(userId2);

        UserArtistTrackCountMapDto artistTrackCount1 = userArtistTrackCountService.fetchUserArtistTrackCount(userId1);
        UserArtistTrackCountMapDto artistTrackCount2 = userArtistTrackCountService.fetchUserArtistTrackCount(userId2);

        return new ComparisonDto(
                commonItems,
                jaccardSimilarity,
                Map.of(userId1, consolidateTracks(topTracks1).stream().toList(), userId2, consolidateTracks(topTracks2).stream().toList()),
                Map.of(userId1, eraSummary1, userId2, eraSummary2),
                Map.of(userId1, featureStats1, userId2, featureStats2),
                Map.of(userId1, genreDistribution1, userId2, genreDistribution2),
                Map.of(userId1, artistTrackCount1, userId2, artistTrackCount2)
        );
    }

    private Map<String, Double> calculateJaccardSimilarity(
            Map<String, List<ArtistDto>> topArtists1,
            Map<String, List<ArtistDto>> topArtists2,
            Map<String, List<TrackDto>> topTracks1,
            Map<String, List<TrackDto>> topTracks2) {

        Set<String> artists1 = consolidateArtists(topArtists1).stream().map(ArtistDto::getId).collect(Collectors.toSet());
        Set<String> artists2 = consolidateArtists(topArtists2).stream().map(ArtistDto::getId).collect(Collectors.toSet());
        double artistSimilarity = calculateJaccard(artists1, artists2);

        Set<String> tracks1 = consolidateTracks(topTracks1).stream().map(TrackDto::getId).collect(Collectors.toSet());
        Set<String> tracks2 = consolidateTracks(topTracks2).stream().map(TrackDto::getId).collect(Collectors.toSet());
        double trackSimilarity = calculateJaccard(tracks1, tracks2);

        Map<String, Double> similarity = new HashMap<>();
        similarity.put("artists", artistSimilarity);
        similarity.put("tracks", trackSimilarity);

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

        Map<String, String> commonArtists = findCommonArtists(topArtists1, topArtists2);
        Map<String, String> commonTracks = findCommonTracks(topTracks1, topTracks2);

        return new CommonItemsDto(commonArtists, commonTracks);
    }

    private Map<String, String> findCommonArtists(
            Map<String, List<ArtistDto>> topArtists1,
            Map<String, List<ArtistDto>> topArtists2) {

        Set<ArtistDto> allArtists1 = consolidateArtists(topArtists1);
        Set<ArtistDto> allArtists2 = consolidateArtists(topArtists2);

        Set<String> commonArtistIds = findCommonIds(allArtists1, allArtists2, ArtistDto::getId);

        return createCommonArtistsMap(allArtists1, commonArtistIds);
    }

    private Set<ArtistDto> consolidateArtists(Map<String, List<ArtistDto>> topArtists) {
        return topArtists.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }

    private <T> Set<String> findCommonIds(Set<T> set1, Set<T> set2, Function<T, String> idExtractor) {
        Set<String> ids1 = set1.stream().map(idExtractor).collect(Collectors.toSet());
        Set<String> ids2 = set2.stream().map(idExtractor).collect(Collectors.toSet());
        Set<String> commonIds = new HashSet<>(ids1);
        commonIds.retainAll(ids2);
        return commonIds;
    }

    private Map<String, String> createCommonArtistsMap(Set<ArtistDto> artists, Set<String> commonArtistIds) {
        return commonArtistIds.stream()
                .collect(Collectors.toMap(
                        artistId -> artistId,
                        artistId -> artists.stream()
                                .filter(a -> a.getId().equals(artistId))
                                .findFirst()
                                .orElseThrow()
                                .getName()
                ));
    }

    private Map<String, String> findCommonTracks(
            Map<String, List<TrackDto>> topTracks1,
            Map<String, List<TrackDto>> topTracks2) {

        Set<TrackDto> allTracks1 = consolidateTracks(topTracks1);
        Set<TrackDto> allTracks2 = consolidateTracks(topTracks2);

        Set<String> commonTrackIds = findCommonIds(allTracks1, allTracks2, TrackDto::getId);

        return createCommonTracksMap(allTracks1, commonTrackIds);
    }

    private Set<TrackDto> consolidateTracks(Map<String, List<TrackDto>> topTracks) {
        return topTracks.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }


    private Map<String, String> createCommonTracksMap(Set<TrackDto> tracks, Set<String> commonTrackIds) {
        return commonTrackIds.stream()
                .collect(Collectors.toMap(
                        trackId -> trackId,
                        trackId -> tracks.stream()
                                .filter(t -> t.getId().equals(trackId))
                                .findFirst()
                                .orElseThrow()
                                .getName()
                ));
    }

}

