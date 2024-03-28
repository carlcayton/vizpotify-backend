package com.arian.vizpotifybackend.unit.services.user;

import com.arian.vizpotifybackend.exception.SpotifyIdNotFoundException;
import com.arian.vizpotifybackend.mapper.UserMapper;
import com.arian.vizpotifybackend.model.JwtResponse;
import com.arian.vizpotifybackend.model.SpotifyAuthToken;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.repository.UserDetailRepository;
import com.arian.vizpotifybackend.services.auth.jwt.JwtService;
import com.arian.vizpotifybackend.services.auth.spotify.SpotifyOauthTokenService;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import com.arian.vizpotifybackend.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDetailRepository userDetailRepository;

    @Mock
    private SpotifyOauthTokenService spotifyOauthTokenService;

    @Mock
    private JwtService jwtService;

    @Mock
    private SpotifyService spotifyService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private SpotifyApi spotifyApi;
    private User spotifyUser;
    private UserDetail userDetail;
    private SpotifyAuthToken spotifyAuthToken;

    @BeforeEach
    void setUp() {
        spotifyApi = mock(SpotifyApi.class);
        spotifyUser = mock(User.class);
        userDetail = new UserDetail();
        spotifyAuthToken = new SpotifyAuthToken();
    }


    @Test
    void loadUserDetailBySpotifyId_shouldReturnUserDetail_whenSpotifyIdExists() {
        String spotifyId = "spotify_id";
        when(userDetailRepository.findBySpotifyId(spotifyId)).thenReturn(Optional.of(userDetail));

        UserDetail result = userService.loadUserDetailBySpotifyId(spotifyId);

        assertNotNull(result);
        assertEquals(userDetail, result);
    }

    @Test
    void loadUserDetailBySpotifyId_shouldThrowException_whenSpotifyIdDoesNotExist() {
        String spotifyId = "spotify_id";
        when(userDetailRepository.findBySpotifyId(spotifyId)).thenReturn(Optional.empty());

        assertThrows(SpotifyIdNotFoundException.class, () -> userService.loadUserDetailBySpotifyId(spotifyId));
    }

    @Test
    void setAnalyticsAvailable_shouldUpdateUserDetail() {
        String userId = "user_id";
        boolean available = true;
        when(userDetailRepository.findBySpotifyId(userId)).thenReturn(Optional.of(userDetail));

        // Act
        userService.setAnalyticsAvailable(userId, available);

        // Assert
        verify(userDetailRepository, times(1)).save(userDetail);
        assertTrue(userDetail.isAnalyticsAvailable());
    }

    @Test
    void findBySpotifyId_shouldReturnOptionalUserDetail_whenSpotifyIdExists() {
        String spotifyId = "spotify_id";
        when(userDetailRepository.findBySpotifyId(spotifyId)).thenReturn(Optional.of(userDetail));

        Optional<UserDetail> result = userService.findBySpotifyId(spotifyId);

        assertTrue(result.isPresent());
        assertEquals(userDetail, result.get());
    }

    @Test
    void findBySpotifyId_shouldReturnEmptyOptional_whenSpotifyIdDoesNotExist() {
        // Arrange
        String spotifyId = "spotify_id";
        when(userDetailRepository.findBySpotifyId(spotifyId)).thenReturn(Optional.empty());

        // Act
        Optional<UserDetail> result = userService.findBySpotifyId(spotifyId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void save_shouldSaveUserDetail() {
        // Arrange
        UserDetail user = new UserDetail();

        // Act
        userService.save(user);

        // Assert
        verify(userDetailRepository, times(1)).save(user);
    }
}