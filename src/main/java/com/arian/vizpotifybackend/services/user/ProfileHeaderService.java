package com.arian.vizpotifybackend.services.user;

import com.arian.vizpotifybackend.dto.ProfileHeaderDTO;
import com.arian.vizpotifybackend.factory.SpotifyApiFactory;
import com.arian.vizpotifybackend.model.SpotifyAuthToken;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.model.UserHeaderStat;
import com.arian.vizpotifybackend.projections.ProfileHeaderProjection;
import com.arian.vizpotifybackend.projections.UserFollowersCountProjection;
import com.arian.vizpotifybackend.repository.SpotifyAuthTokenRepository;
import com.arian.vizpotifybackend.repository.UserHeaderStatRepository;
import com.arian.vizpotifybackend.repository.UserRepository;
import com.arian.vizpotifybackend.services.auth.spotify.SpotifyOauthTokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.requests.data.follow.GetUsersFollowedArtistsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProfileHeaderService {

    private final UserHeaderStatRepository userHeaderStatRepository;
    private final UserRepository userRepository;
    private final SpotifyApiFactory spotifyApiFactory;
    private final SpotifyOauthTokenService spotifyOauthTokenService;
    private final SpotifyAuthTokenRepository spotifyAuthTokenRepository;

    public ProfileHeaderDTO getProfileHeaderDTO(String spotifyId) {
        Optional<UserHeaderStat> userHeaderStatOptional = userHeaderStatRepository.findById(spotifyId);
        if (userHeaderStatOptional.isEmpty()) {
            userHeaderStatRepository.save(createUserHeaderStat(spotifyId));
        }
        ProfileHeaderProjection projection = userHeaderStatRepository.getProfileHeaderBySpotifyId(spotifyId);
        if (projection != null) {
            return ProfileHeaderDTO.builder()
                    .spotifyId(projection.getSpotifyId())
                    .followedArtistCount(projection.getFollowedArtistCount())
                    .followerCount(projection.getFollowerCount())
                    .playlistCount(projection.getPlaylistCount())
                    .profilePictureUrl(projection.getProfilePictureUrl())
                    .userDisplayName(projection.getUserDisplayName())
                    .build();
        }
        return null;
    }

    private UserHeaderStat createUserHeaderStat(String spotifyId) {
        SpotifyAuthToken spotifyAuthToken = spotifyAuthTokenRepository
                .findById(spotifyId)
                .orElseThrow(() -> new RuntimeException(""));
        SpotifyApi spotifyApi = spotifyApiFactory.createSpotifyApiWithAccessToken(spotifyAuthToken.getAccessToken());
        int playlistCount = getPlaylistCount(spotifyApi);
        int followedArtistCount = getFollowedArtistsCount(spotifyApi);
        int followerCount = getFollowerCount(spotifyId);
        UserHeaderStat userHeaderStat = UserHeaderStat.builder()
                .userSpotifyId(spotifyId)
                .playlistCount(playlistCount)
                .followerCount(followerCount)
                .followedArtistCount(followedArtistCount)
                .build();
        userHeaderStatRepository.save(userHeaderStat);
        return userHeaderStat;
    }

    private int getPlaylistCount(SpotifyApi spotifyApi) {
        try {
            final GetListOfCurrentUsersPlaylistsRequest getListOfCurrentUsersPlaylistsRequest = spotifyApi.getListOfCurrentUsersPlaylists()
                    .limit(1)
                    .build();
            final CompletableFuture<Paging<PlaylistSimplified>> pagingFuture = getListOfCurrentUsersPlaylistsRequest.executeAsync();
            final Paging<PlaylistSimplified> playlistSimplifiedPaging = pagingFuture.join();

            return playlistSimplifiedPaging.getTotal();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getFollowerCount(String spotifyId) {
        UserFollowersCountProjection projection = userRepository.findFollowersCountBySpotifyId(spotifyId)
                .orElseThrow(() -> new EntityNotFoundException("User with Spotify ID " + spotifyId + " not found"));

        return projection.getFollowersCount();
    }

    private int getFollowedArtistsCount(SpotifyApi spotifyApi) {
        try {
            final GetUsersFollowedArtistsRequest getListOfCurrentUsersPlaylistsRequest = spotifyApi.getUsersFollowedArtists(ModelObjectType.ARTIST)
                    .after("0LcJLqbBmaGUft1e9Mm8HV")
                    .limit(1)
                    .build();
            final CompletableFuture<PagingCursorbased<Artist>> pagingCursorBasedFuture = getListOfCurrentUsersPlaylistsRequest.executeAsync();
            final PagingCursorbased<Artist> artistPagingCursorbased = pagingCursorBasedFuture.join();

            return artistPagingCursorbased.getTotal();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}

