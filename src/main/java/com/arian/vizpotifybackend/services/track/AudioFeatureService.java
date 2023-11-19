package com.arian.vizpotifybackend.services.track;

import com.arian.vizpotifybackend.model.AudioFeature;
import com.arian.vizpotifybackend.model.TrackDetail;
import com.arian.vizpotifybackend.repository.AudioFeatureRepository;
import com.arian.vizpotifybackend.repository.TrackDetailRepository;
import com.arian.vizpotifybackend.services.redis.TrackCacheService;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AudioFeatureService {

    private final SpotifyService spotifyService;
    private final AudioFeatureRepository audioFeatureRepository;
    private final TrackCacheService trackCacheService;
    private final TrackDetailRepository trackDetailRepository;

    public Optional<AudioFeature> getAudioFeature(String trackId) {
        Optional<AudioFeature> audioFeature = trackCacheService.getAudioFeaturesFromCache(trackId);

        if (audioFeature.isPresent()) {
            return audioFeature;
        }

        audioFeature = audioFeatureRepository.findById(trackId);
        if (audioFeature.isPresent()) {
            trackCacheService.cacheAudioFeatures(trackId, audioFeature.get());
            return audioFeature;
        }
        return Optional.empty();
    }
    @Scheduled(fixedRate = 1000*60*60*24) // Preload everyday
    public void preloadAudioFeatures() {
        List<String> trackIds = getTracksForPreloading();

        final int BATCH_SIZE = 100; // Set an appropriate batch size
        for (int i = 0; i < trackIds.size(); i += BATCH_SIZE) {
            List<String> batch = trackIds.subList(i, Math.min(i + BATCH_SIZE, trackIds.size()));
            fetchAndCacheBatch(batch);
        }
    }

    private void fetchAndCacheBatch(List<String> trackIds) {
        List<AudioFeature> audioFeatures = audioFeatureRepository.findAllById(trackIds);
        trackCacheService.cacheAudioFeaturesBatch(audioFeatures);
    }

    public void saveAudioFeaturesForSeveralTracks(List<String> ids){
        AudioFeatures[] audioFeaturesArray = getAudioFeaturesForSeveralTracksFromSpotify(ids);

        if (audioFeaturesArray != null) {
            List<AudioFeature> audioFeaturesList = Arrays.stream(audioFeaturesArray)
                    .map(AudioFeatureService::toAudioFeature)
                    .collect(Collectors.toList());
            audioFeatureRepository.saveAll(audioFeaturesList);
        }
        preloadAudioFeatures();
    }

    private AudioFeatures[] getAudioFeaturesForSeveralTracksFromSpotify(List<String> ids){
        return spotifyService.getAudioFeaturesForSeveralTracks(ids);
    }
    public static AudioFeature toAudioFeature(AudioFeatures audioFeatures) {
        return AudioFeature.builder()
                .id(audioFeatures.getId())
                .acousticness(audioFeatures.getAcousticness())
                .danceability(audioFeatures.getDanceability())
                .energy(audioFeatures.getEnergy())
                .instrumentalness(audioFeatures.getInstrumentalness())
                .liveness(audioFeatures.getLiveness())
                .speechiness(audioFeatures.getSpeechiness())
                .valence(audioFeatures.getValence())
                .tempo(audioFeatures.getTempo())
                .build();
    }

    private List<String> getTracksForPreloading() {
        // Fetch tracks with popularity >= 70
        return trackDetailRepository.findByPopularityGreaterThanEqual(70)
                .stream()
                .map(TrackDetail::getId) // Assuming Track has a getId method
                .collect(Collectors.toList());
    }
}