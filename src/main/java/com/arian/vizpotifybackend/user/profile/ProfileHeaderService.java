package com.arian.vizpotifybackend.user.profile;

import com.arian.vizpotifybackend.common.SpotifyService;
import com.arian.vizpotifybackend.user.core.UserDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileHeaderService {

    private final UserHeaderStatRepository userHeaderStatRepository;
    private final UserDetailRepository userDetailRepository;
    private final SpotifyService spotifyService;

    public ProfileHeaderDto getProfileHeaderDto(String spotifyId) {
        Optional<UserHeaderStat> userHeaderStatOptional = userHeaderStatRepository.findById(spotifyId);
        if (userHeaderStatOptional.isEmpty()) {
            userHeaderStatRepository.save(createUserHeaderStat(spotifyId));
        }
        ProfileHeaderProjection projection = userHeaderStatRepository.getProfileHeaderBySpotifyId(spotifyId);
        if (projection != null) {
            return ProfileHeaderDto.builder()
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
        int followedArtistCount = spotifyService.getFollowedArtists(spotifyId).getTotal();
        int followerCount = userDetailRepository.findFollowersCountBySpotifyId(spotifyId);
        UserHeaderStat userHeaderStat = UserHeaderStat.builder()
                .userSpotifyId(spotifyId)
                .followerCount(followerCount)
                .followedArtistCount(followedArtistCount)
                .build();
        userHeaderStatRepository.save(userHeaderStat);
        return userHeaderStat;
    }


}

