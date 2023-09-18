package com.arian.vizpotifybackend.services.spotify;

import com.arian.vizpotifybackend.factory.SpotifyApiFactory;
import com.arian.vizpotifybackend.model.ArtistDetail;
import com.arian.vizpotifybackend.model.SpotifyAuthToken;
import com.arian.vizpotifybackend.repository.SpotifyAuthTokenRepository;
import com.arian.vizpotifybackend.services.auth.spotify.SpotifyOauthTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.follow.GetUsersFollowedArtistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class SpotifyService {


    private final SpotifyApiFactory spotifyApiFactory;
    private final SpotifyOauthTokenService spotifyOauthTokenService;
    private final SpotifyAuthTokenRepository spotifyAuthTokenRepository;

    public Paging<PlaylistSimplified> getPlaylist(String spotifyId){
        SpotifyAuthToken spotifyAuthToken = spotifyAuthTokenRepository
                .findById(spotifyId)
                .orElseThrow(() -> new RuntimeException(""));
        SpotifyApi spotifyApi = spotifyApiFactory.createSpotifyApiWithAccessToken(spotifyAuthToken.getAccessToken());
        try {
            final GetListOfCurrentUsersPlaylistsRequest getListOfCurrentUsersPlaylistsRequest = spotifyApi.getListOfCurrentUsersPlaylists()
                    .limit(1)
                    .build();
            final CompletableFuture<Paging<PlaylistSimplified>> pagingFuture = getListOfCurrentUsersPlaylistsRequest.executeAsync();
            return pagingFuture.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PagingCursorbased<Artist> getFollowedArtists(String spotifyId){
        SpotifyAuthToken spotifyAuthToken = spotifyAuthTokenRepository
                .findById(spotifyId)
                .orElseThrow(() -> new RuntimeException(""));
        SpotifyApi spotifyApi = spotifyApiFactory.createSpotifyApiWithAccessToken(spotifyAuthToken.getAccessToken());
        try {
            final GetUsersFollowedArtistsRequest getListOfCurrentUsersPlaylistsRequest = spotifyApi.getUsersFollowedArtists(ModelObjectType.ARTIST)
                    .after("0LcJLqbBmaGUft1e9Mm8HV")
                    .limit(1)
                    .build();
            final CompletableFuture<PagingCursorbased<Artist>> pagingCursorBasedFuture = getListOfCurrentUsersPlaylistsRequest.executeAsync();

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


}
