package com.arian.vizpotifybackend.common;

import com.arian.vizpotifybackend.common.config.SpotifyConfig;

import com.arian.vizpotifybackend.auth.SpotifyAuthToken;
import com.arian.vizpotifybackend.auth.SpotifyAuthTokenRepository;
import com.arian.vizpotifybackend.common.util.SpotifyUtil;
import com.neovisionaries.i18n.CountryCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsRelatedArtistsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import se.michaelthelin.spotify.requests.data.follow.GetUsersFollowedArtistsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioFeaturesForSeveralTracksRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
public class SpotifyService {


    private final SpotifyAuthTokenRepository spotifyAuthTokenRepository;
    private final SpotifyConfig spotifyConfig;
    private final SpotifyUtil spotifyUtil;

    public Paging<PlaylistSimplified> getPlaylist(String spotifyId) {
        SpotifyApi spotifyApi = getSpotifyApi(spotifyId);
        try {
            final GetListOfCurrentUsersPlaylistsRequest getListOfCurrentUsersPlaylistsRequest = spotifyApi.getListOfCurrentUsersPlaylists().limit(1).build();
            final CompletableFuture<Paging<PlaylistSimplified>> pagingFuture = getListOfCurrentUsersPlaylistsRequest.executeAsync();
            return pagingFuture.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PagingCursorbased<Artist> getFollowedArtists(String spotifyId) {
        SpotifyApi spotifyApi = getSpotifyApi(spotifyId);
        try {
            final GetUsersFollowedArtistsRequest getUsersFollowedArtistsRequest = spotifyApi.getUsersFollowedArtists(ModelObjectType.ARTIST).after("0LcJLqbBmaGUft1e9Mm8HV").limit(1).build();
            final CompletableFuture<PagingCursorbased<Artist>> pagingCursorBasedFuture = getUsersFollowedArtistsRequest.executeAsync();

            return pagingCursorBasedFuture.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserProfile(SpotifyApi spotifyApi) {
        GetCurrentUsersProfileRequest request = spotifyApi.getCurrentUsersProfile().build();
        return request.executeAsync().join();
    }

    public Map<TimeRange, Paging<Artist>> getUserTopArtistsForAllTimeRange(String spotifyId) {
        Map<TimeRange, Paging<Artist>> output = new HashMap<>();
        SpotifyApi spotifyApi = getSpotifyApi(spotifyId);
        for (TimeRange timeRange : TimeRange.values()) {
            output.put(timeRange, getUserTopArtists(spotifyApi, timeRange.getValue()));
        }
        return output;
    }
    public Map<TimeRange, Paging<Track>> getUserTopTracksForAllTimeRange(String spotifyId) {
        Map<TimeRange, Paging<Track>> output = new HashMap<>();
        SpotifyApi spotifyApi = getSpotifyApi(spotifyId); // Method to create or retrieve SpotifyApi instance

        for (TimeRange timeRange : TimeRange.values()) {
            Paging<Track> topTracksPaging = getUserTopTracks(spotifyApi, timeRange.getValue());
            if (topTracksPaging != null) {
                output.put(timeRange, topTracksPaging);
            }
        }

        return output;
    }

    public Artist[] getRelatedArtists(String artistId) {
        SpotifyApi spotifyApi = spotifyConfig.spotifyApiWithClientCredentials();
        try {
            final GetArtistsRelatedArtistsRequest getArtistsRelatedArtistsRequest = spotifyApi.getArtistsRelatedArtists(artistId).build();
            CompletableFuture<Artist[]> artistsFuture = getArtistsRelatedArtistsRequest.executeAsync();
            return artistsFuture.join();
        } catch (Exception e) {
            // Log the exception
        }
        return new Artist[0];
    }

    public Track[] getArtistTopTracks(String artistId) {
        SpotifyApi spotifyApi = spotifyConfig.spotifyApiWithClientCredentials();
        try {
            final GetArtistsTopTracksRequest getArtistsTopTracksRequest = spotifyApi.getArtistsTopTracks(artistId, CountryCode.US).build();
            CompletableFuture<Track[]> tracksFuture = getArtistsTopTracksRequest.executeAsync();
            return tracksFuture.join();
        } catch (Exception e) {
            // Log the exception
        }
        return new Track[0];
    }


    private Paging<Artist> getUserTopArtists(SpotifyApi spotifyApi, String timeRange) {
        try {
            GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists()
                    .time_range(timeRange)
                    .limit(50)
                    .build();
            final CompletableFuture<Paging<Artist>> pagingFuture = getUsersTopArtistsRequest.executeAsync();
            return pagingFuture.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AudioFeatures[] getAudioFeaturesForSeveralTracks(List<String> ids) {
        SpotifyApi spotifyApi = spotifyConfig.spotifyApiWithClientCredentials();
        try {
            GetAudioFeaturesForSeveralTracksRequest
                    getAudioFeaturesForSeveralTracksRequest =
                    spotifyApi.getAudioFeaturesForSeveralTracks(spotifyUtil.listToCsv(ids))
                            .build();
            final CompletableFuture<AudioFeatures[]> audioFeaturesFuture = getAudioFeaturesForSeveralTracksRequest.executeAsync();
            return audioFeaturesFuture.join();

        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
        return null;
    }

    public Paging<Track> getUserTopTracks(SpotifyApi spotifyApi, String timeRange) {
        try {

            GetUsersTopTracksRequest getUsersTopTracksRequest = spotifyApi.getUsersTopTracks()
                    .time_range(timeRange)
                    .limit(50)
                    .build();
            final CompletableFuture<Paging<Track>> pagingFuture = getUsersTopTracksRequest.executeAsync();
            return pagingFuture.join();

        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
        return null;
    }


    private SpotifyApi getSpotifyApi(String spotifyId) {
        SpotifyAuthToken spotifyAuthToken = spotifyAuthTokenRepository.findById(spotifyId)
            .orElseThrow(() -> new RuntimeException("SpotifyAuthToken not found for id: " + spotifyId));
        return spotifyConfig.spotifyApiWithAccessToken(spotifyAuthToken.getAccessToken());
    }


}
