package com.arian.vizpotifybackend.services.track;

import com.arian.vizpotifybackend.mapper.AudioFeatureMapper;
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

import java.util.ArrayList;
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
    private final AudioFeatureMapper audioFeatureMapper;

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
    @Scheduled(fixedRate = 1000*60*60)
    public void preloadAudioFeatures() {
        List<String> trackIds = getTracksForPreloading();

        final int BATCH_SIZE = 100;
        for (int i = 0; i < trackIds.size(); i += BATCH_SIZE) {
            List<String> batch = trackIds.subList(i, Math.min(i + BATCH_SIZE, trackIds.size()));
            fetchAndCacheBatch(batch);
        }
    }

    private void fetchAndCacheBatch(List<String> trackIds) {
        List<AudioFeature> audioFeatures = audioFeatureRepository.findAllById(trackIds);
        trackCacheService.cacheAudioFeaturesBatch(audioFeatures);
    }

    public void saveAudioFeaturesForSeveralTracks(List<String> ids) {
        final int BATCH_SIZE = 20;
        List<AudioFeature> allAudioFeatures = new ArrayList<>(ids.size());

        for (int i = 0; i < ids.size(); i += BATCH_SIZE) {
            List<String> batch = ids.subList(i, Math.min(ids.size(), i + BATCH_SIZE));

            AudioFeatures[] audioFeaturesArray = getAudioFeaturesForSeveralTracksFromSpotify(batch);

            if (audioFeaturesArray != null) {
                List<AudioFeature> audioFeaturesList = Arrays.stream(audioFeaturesArray)
                        .map(audioFeatureMapper::toAudioFeature)
                        .collect(Collectors.toList());
                allAudioFeatures.addAll(audioFeaturesList);
            }
        }

        if (!allAudioFeatures.isEmpty()) {
            audioFeatureRepository.saveAll(allAudioFeatures);
        }

        preloadAudioFeatures();
    }


    private AudioFeatures[] getAudioFeaturesForSeveralTracksFromSpotify(List<String> ids){
        return spotifyService.getAudioFeaturesForSeveralTracks(ids);
    }

    private List<String> getTracksForPreloading() {
        return trackDetailRepository.findByPopularityGreaterThanEqual(70)
                .stream()
                .map(TrackDetail::getId)
                .collect(Collectors.toList());
    }
}
