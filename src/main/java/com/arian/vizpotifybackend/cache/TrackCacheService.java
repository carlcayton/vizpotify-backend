package com.arian.vizpotifybackend.cache;

import com.arian.vizpotifybackend.track.AudioFeature;
import com.arian.vizpotifybackend.track.TrackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackCacheService {

    private final RedisTemplate<String, Object> jsonRedisTemplate;
    private static final String TOP_TRACKS_KEY_PREFIX = "artist:toptracks:";
    private static final String AUDIO_FEATURES_KEY_PREFIX = "track:audiofeatures:";

    public void cacheArtistTopTracks(String artistId, List<TrackDto> topTracks) {
        String key = TOP_TRACKS_KEY_PREFIX + artistId;
        jsonRedisTemplate.opsForValue().set(key, topTracks, 1, TimeUnit.DAYS); // Cache for 1 day, adjust as needed
    }

    public Optional<List<TrackDto>> getArtistTopTracksFromCache(String artistId) {
        String key = TOP_TRACKS_KEY_PREFIX + artistId;
        return Optional.ofNullable((List<TrackDto>) jsonRedisTemplate.opsForValue().get(key));
    }

    public void cacheAudioFeatures(String trackId, AudioFeature audioFeature) {
        String key = AUDIO_FEATURES_KEY_PREFIX + trackId;
        jsonRedisTemplate.opsForValue().set(key, audioFeature, 1, TimeUnit.DAYS); // Cache for 1 day
    }

    public Optional<AudioFeature> getAudioFeaturesFromCache(String trackId) {
        String key = AUDIO_FEATURES_KEY_PREFIX + trackId;
        return Optional.ofNullable((AudioFeature) jsonRedisTemplate.opsForValue().get(key));
    }

    public void cacheAudioFeaturesBatch(List<AudioFeature> audioFeatures) {

        Map<String, AudioFeature> audioFeaturesMap = audioFeatures.stream()
                .collect(Collectors.toMap(
                        audioFeature -> AUDIO_FEATURES_KEY_PREFIX + audioFeature.getId(),
                        Function.identity()
                ));

        jsonRedisTemplate.opsForValue().multiSet(audioFeaturesMap);
    }
}
