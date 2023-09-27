package com.arian.vizpotifybackend.services.spotify;

import com.arian.vizpotifybackend.enums.TimeRange;
import com.arian.vizpotifybackend.factory.SpotifyApiFactory;
import com.arian.vizpotifybackend.model.ArtistDetail;
import com.arian.vizpotifybackend.model.SpotifyAuthToken;
import com.arian.vizpotifybackend.repository.SpotifyAuthTokenRepository;
import com.arian.vizpotifybackend.services.auth.spotify.SpotifyOauthTokenService;
import com.neovisionaries.i18n.CountryCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.IModelObject;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.*;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsRelatedArtistsRequest;
import se.michaelthelin.spotify.requests.data.artists.GetArtistsTopTracksRequest;
import se.michaelthelin.spotify.requests.data.follow.GetUsersFollowedArtistsRequest;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class SpotifyService {


    private final SpotifyApiFactory spotifyApiFactory;
    private final SpotifyAuthTokenRepository spotifyAuthTokenRepository;

    public Paging<PlaylistSimplified> getPlaylist(String spotifyId){
        SpotifyApi spotifyApi = getSpotifyApi(spotifyId);
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
        SpotifyApi spotifyApi = getSpotifyApi(spotifyId);
        try {
            final GetUsersFollowedArtistsRequest getUsersFollowedArtistsRequest= spotifyApi.getUsersFollowedArtists(ModelObjectType.ARTIST)
                    .after("0LcJLqbBmaGUft1e9Mm8HV")
                    .limit(1)
                    .build();
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

    public Map<TimeRange, Paging<Artist>> getUserTopArtistsForAllTimeRange(String spotifyId){
        Map<TimeRange, Paging<Artist>> output = new HashMap<>();
        SpotifyApi spotifyApi = getSpotifyApi(spotifyId);
        for (TimeRange timeRange: TimeRange.values()){
            output.put(timeRange, getUserTopArtists(spotifyApi,timeRange.getValue()));
        }
        return output;
    }

    public Artist[] getRelatedArtists(String artistId){
        SpotifyApi spotifyApi = spotifyApiFactory.createSpotifyApiWithClientCredentials();
        try {
            final GetArtistsRelatedArtistsRequest getArtistsRelatedArtistsRequest = spotifyApi
                    .getArtistsRelatedArtists(artistId)
                    .build();
            CompletableFuture<Artist[]> artistsFuture= getArtistsRelatedArtistsRequest.executeAsync();
            return artistsFuture.join();
        }catch (Exception e){

        }
        return null;
    }
    public Track[] getArtistTopTracks(String artistId){

        SpotifyApi spotifyApi = spotifyApiFactory.createSpotifyApiWithClientCredentials();
        try {
            final GetArtistsTopTracksRequest getArtistsTopTracksRequest= spotifyApi
                    .getArtistsTopTracks(artistId, CountryCode.US)
                    .build();
            CompletableFuture<Track[]> tracksFuture= getArtistsTopTracksRequest.executeAsync();
            return tracksFuture.join();
        }catch (Exception e){

        }
        return null;
    }

    private Paging<Artist> getUserTopArtists(SpotifyApi spotifyApi, String timeRange){
        try{
            GetUsersTopArtistsRequest getUsersTopArtistsRequest = spotifyApi.getUsersTopArtists()
          .time_range(timeRange)
                    .limit(30)
                    .build();
            final CompletableFuture<Paging<Artist>> pagingFuture = getUsersTopArtistsRequest.executeAsync();
            return pagingFuture.join();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    private SpotifyApi getSpotifyApi(String spotifyId){
        SpotifyAuthToken spotifyAuthToken = spotifyAuthTokenRepository
                .findById(spotifyId)
                .orElseThrow(() -> new RuntimeException(""));
        return spotifyApiFactory.createSpotifyApiWithAccessToken(spotifyAuthToken.getAccessToken());
    }


}
