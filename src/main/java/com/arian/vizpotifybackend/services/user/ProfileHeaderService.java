package com.arian.vizpotifybackend.services.user;

import com.arian.vizpotifybackend.dto.ProfileHeaderDTO;
import com.arian.vizpotifybackend.model.UserHeaderStat;
import com.arian.vizpotifybackend.projections.ProfileHeaderProjection;
import com.arian.vizpotifybackend.projections.UserFollowersCountProjection;
import com.arian.vizpotifybackend.repository.UserHeaderStatRepository;
import com.arian.vizpotifybackend.repository.UserDetailRepository;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileHeaderService {

    private final UserHeaderStatRepository userHeaderStatRepository;
    private final UserDetailRepository userDetailRepository;
    private final SpotifyService spotifyService;

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
//        int playlistCount = spotifyService.getPlaylist(spotifyId).getTotal();
        int followedArtistCount = spotifyService.getFollowedArtists(spotifyId).getTotal();
        int followerCount = userDetailRepository.findFollowersCountBySpotifyId(spotifyId);
        UserHeaderStat userHeaderStat = UserHeaderStat.builder()
                .userSpotifyId(spotifyId)
//                .playlistCount(playlistCount)
                .followerCount(followerCount)
                .followedArtistCount(followedArtistCount)
                .build();
        userHeaderStatRepository.save(userHeaderStat);
        return userHeaderStat;
    }


}

