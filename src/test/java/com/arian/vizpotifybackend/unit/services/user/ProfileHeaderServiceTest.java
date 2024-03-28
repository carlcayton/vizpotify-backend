package com.arian.vizpotifybackend.unit.services.user;

import com.arian.vizpotifybackend.dto.ProfileHeaderDTO;
import com.arian.vizpotifybackend.model.UserHeaderStat;
import com.arian.vizpotifybackend.projections.ProfileHeaderProjection;
import com.arian.vizpotifybackend.repository.UserHeaderStatRepository;
import com.arian.vizpotifybackend.repository.UserDetailRepository;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import com.arian.vizpotifybackend.services.user.ProfileHeaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileHeaderServiceTest {

    @Mock
    private UserHeaderStatRepository userHeaderStatRepository;

    @Mock
    private UserDetailRepository userDetailRepository;

    @Mock
    private SpotifyService spotifyService;

    @InjectMocks
    private ProfileHeaderService profileHeaderService;

    private String spotifyId;
    private UserHeaderStat userHeaderStat;
    private ProfileHeaderProjection profileHeaderProjection;

    @BeforeEach
    void setUp() {
        spotifyId = "user123";
        userHeaderStat = UserHeaderStat.builder()
                .userSpotifyId(spotifyId)
                .followerCount(100)
                .followedArtistCount(50)
                .build();
        profileHeaderProjection = mock(ProfileHeaderProjection.class);
    }

    @Test
    void getProfileHeaderDTO_shouldReturnProfileHeaderDTO_whenUserHeaderStatExists() {
        when(userHeaderStatRepository.findById(spotifyId)).thenReturn(Optional.of(userHeaderStat));
        when(userHeaderStatRepository.getProfileHeaderBySpotifyId(spotifyId)).thenReturn(profileHeaderProjection);
        when(profileHeaderProjection.getSpotifyId()).thenReturn(spotifyId);
        when(profileHeaderProjection.getFollowedArtistCount()).thenReturn(50);
        when(profileHeaderProjection.getFollowerCount()).thenReturn(100);
        when(profileHeaderProjection.getPlaylistCount()).thenReturn(10);
        when(profileHeaderProjection.getProfilePictureUrl()).thenReturn("profile_url");
        when(profileHeaderProjection.getUserDisplayName()).thenReturn("John Doe");

        ProfileHeaderDTO result = profileHeaderService.getProfileHeaderDTO(spotifyId);

        verify(userHeaderStatRepository, times(1)).findById(spotifyId);
        verify(userHeaderStatRepository, times(1)).getProfileHeaderBySpotifyId(spotifyId);

        assertEquals(spotifyId, result.getSpotifyId());
        assertEquals(50, result.getFollowedArtistCount());
        assertEquals(100, result.getFollowerCount());
        assertEquals(10, result.getPlaylistCount());
        assertEquals("profile_url", result.getProfilePictureUrl());
        assertEquals("John Doe", result.getUserDisplayName());
    }



    @Test
    void getProfileHeaderDTO_shouldReturnNull_whenProfileHeaderProjectionIsNull() {
        when(userHeaderStatRepository.findById(spotifyId)).thenReturn(Optional.of(userHeaderStat));
        when(userHeaderStatRepository.getProfileHeaderBySpotifyId(spotifyId)).thenReturn(null);

        ProfileHeaderDTO result = profileHeaderService.getProfileHeaderDTO(spotifyId);

        verify(userHeaderStatRepository, times(1)).findById(spotifyId);
        verify(userHeaderStatRepository, times(1)).getProfileHeaderBySpotifyId(spotifyId);

        assertNull(result);
    }
}