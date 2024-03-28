package com.arian.vizpotifybackend.unit.services.user;

import com.arian.vizpotifybackend.dto.TrackDTO;
import com.arian.vizpotifybackend.enums.TimeRange;
import com.arian.vizpotifybackend.mapper.TrackMapper;
import com.arian.vizpotifybackend.model.TrackDetail;
import com.arian.vizpotifybackend.model.UserTopTrack;
import com.arian.vizpotifybackend.repository.UserTopTrackRepository;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import com.arian.vizpotifybackend.services.track.TrackDetailService;
import com.arian.vizpotifybackend.services.user.UserTopTrackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserTopTrackServiceTest {

    @Mock
    private UserTopTrackRepository userTopTrackRepository;

    @Mock
    private SpotifyService spotifyService;

    @Mock
    private TrackDetailService trackDetailService;

    @Mock
    private TrackMapper trackMapper;

    @InjectMocks
    private UserTopTrackService userTopTrackService;

    private String userId;
    private Map<TimeRange, Paging<Track>> userTopTracksForAllTimeRange;
    private List<UserTopTrack> userTopTrackList;
    private List<TrackDetail> trackDetailList;

    @BeforeEach
    void setUp() {
        userId = "user123";
        userTopTracksForAllTimeRange = new HashMap<>();

        Paging<Track> trackPaging = mock(Paging.class);
        Track[] tracks = new Track[]{mock(Track.class)};
        when(trackPaging.getItems()).thenReturn(tracks);
        userTopTracksForAllTimeRange.put(TimeRange.SHORT_TERM, trackPaging);

        userTopTrackList = new ArrayList<>();
        trackDetailList = new ArrayList<>();
    }

    @Test
    void getUserTopItems_shouldFetchFromDB_whenUserExists() {
        when(userTopTrackRepository.existsByUserSpotifyId(userId)).thenReturn(true);
        when(userTopTrackRepository.findByUserSpotifyId(userId)).thenReturn(userTopTrackList);
        when(trackDetailService.getTracksByIds(anyList())).thenReturn(trackDetailList);

        Map<String, List<TrackDTO>> result = userTopTrackService.getUserTopItems(userId);

        verify(userTopTrackRepository, times(1)).existsByUserSpotifyId(userId);
        verify(userTopTrackRepository, times(1)).findByUserSpotifyId(userId);
        verify(trackDetailService, times(1)).getTracksByIds(anyList());
        verify(trackMapper, times(userTopTrackList.size())).trackDetailToTrackDTO(any(TrackDetail.class));

        assertEquals(userTopTrackList.size(), result.values().stream().mapToInt(List::size).sum());
    }

    @Test
    void getUserTopItems_shouldFetchFromSpotifyAndSave_whenUserDoesNotExist() {
        when(userTopTrackRepository.existsByUserSpotifyId(userId)).thenReturn(false);
        when(spotifyService.getUserTopTracksForAllTimeRange(userId)).thenReturn(userTopTracksForAllTimeRange);
        when(trackDetailService.extractUniqueTracks(userTopTracksForAllTimeRange)).thenReturn(new HashSet<>());
        when(trackMapper.trackToTrackDTO(any(Track.class))).thenReturn(new TrackDTO());

        Map<String, List<TrackDTO>> result = userTopTrackService.getUserTopItems(userId);

        verify(userTopTrackRepository, times(1)).existsByUserSpotifyId(userId);
        verify(spotifyService, times(1)).getUserTopTracksForAllTimeRange(userId);
        verify(trackDetailService, times(1)).extractUniqueTracks(userTopTracksForAllTimeRange);
        verify(trackDetailService, times(1)).processAndStoreNewTrackDetails(anySet());

        ArgumentCaptor<List<UserTopTrack>> userTopTrackCaptor = ArgumentCaptor.forClass(List.class);
        verify(userTopTrackRepository, times(1)).saveAll(userTopTrackCaptor.capture());

        List<UserTopTrack> savedUserTopTracks = userTopTrackCaptor.getValue();
        assertEquals(userTopTracksForAllTimeRange.values().stream().mapToInt(paging -> paging.getItems().length).sum(),
                savedUserTopTracks.size());

        assertEquals(userTopTracksForAllTimeRange.size(), result.size());
    }


}