package com.arian.vizpotifybackend.unit.services.track;

import com.arian.vizpotifybackend.cache.TrackCacheService;
import com.arian.vizpotifybackend.common.SpotifyService;
import com.arian.vizpotifybackend.common.mapper.AudioFeatureMapper;
import com.arian.vizpotifybackend.track.AudioFeature;
import com.arian.vizpotifybackend.track.AudioFeatureRepository;
import com.arian.vizpotifybackend.track.AudioFeatureService;
import com.arian.vizpotifybackend.track.TrackDetailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AudioFeatureServiceTest {

    @Mock
    private SpotifyService spotifyService;

    @Mock
    private AudioFeatureRepository audioFeatureRepository;

    @Mock
    private TrackCacheService trackCacheService;

    @Mock
    private TrackDetailRepository trackDetailRepository;

    @Mock
    private AudioFeatureMapper audioFeatureMapper;

    @InjectMocks
    private AudioFeatureService audioFeatureService;

    private String trackId;
    private AudioFeature audioFeature;

    @BeforeEach
    void setUp() {
        trackId = "track123";
        audioFeature = new AudioFeature();
        audioFeature.setId(trackId);
    }

    @Test
    void getAudioFeature_shouldReturnCachedAudioFeature_whenAvailable() {
        when(trackCacheService.getAudioFeaturesFromCache(trackId)).thenReturn(Optional.of(audioFeature));

        Optional<AudioFeature> result = audioFeatureService.getAudioFeature(trackId);

        assertTrue(result.isPresent());
        assertEquals(audioFeature, result.get());
        verify(audioFeatureRepository, never()).findById(trackId);
    }

    @Test
    void getAudioFeature_shouldReturnAudioFeatureFromDB_whenNotCached() {
        when(trackCacheService.getAudioFeaturesFromCache(trackId)).thenReturn(Optional.empty());
        when(audioFeatureRepository.findById(trackId)).thenReturn(Optional.of(audioFeature));

        Optional<AudioFeature> result = audioFeatureService.getAudioFeature(trackId);

        assertTrue(result.isPresent());
        assertEquals(audioFeature, result.get());
        verify(audioFeatureRepository).findById(trackId);
        verify(trackCacheService).cacheAudioFeatures(trackId, audioFeature);
    }

    @Test
    void getAudioFeature_shouldReturnEmpty_whenNotAvailable() {
        when(trackCacheService.getAudioFeaturesFromCache(trackId)).thenReturn(Optional.empty());
        when(audioFeatureRepository.findById(trackId)).thenReturn(Optional.empty());

        Optional<AudioFeature> result = audioFeatureService.getAudioFeature(trackId);

        assertFalse(result.isPresent());
    }

    @Test
    void saveAudioFeaturesForSeveralTracks_shouldSaveAudioFeatures() {
        List<String> trackIds = Arrays.asList("track1", "track2");
        AudioFeatures audioFeature1 = new AudioFeatures.Builder().build();
        AudioFeatures audioFeature2 = new AudioFeatures.Builder().build();
        AudioFeatures[] audioFeaturesArray = {audioFeature1, audioFeature2};

        when(spotifyService.getAudioFeaturesForSeveralTracks(trackIds)).thenReturn(audioFeaturesArray);
        when(audioFeatureMapper.toAudioFeature(any(AudioFeatures.class))).thenReturn(new AudioFeature());

        audioFeatureService.saveAudioFeaturesForSeveralTracks(trackIds);

        verify(spotifyService).getAudioFeaturesForSeveralTracks(trackIds);
        verify(audioFeatureMapper, times(2)).toAudioFeature(any(AudioFeatures.class));
        verify(audioFeatureRepository).saveAll(anyList());
    }

}