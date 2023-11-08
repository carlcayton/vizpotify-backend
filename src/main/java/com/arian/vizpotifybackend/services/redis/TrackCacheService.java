package com.arian.vizpotifybackend.services.redis;

import com.arian.vizpotifybackend.dto.TrackDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TrackCacheService {

    private final RedisTemplate<String, Object> jsonRedisTemplate;
    private static final String TOP_TRACKS_KEY_PREFIX = "artist:toptracks:";

    public void cacheArtistTopTracks(String artistId, List<TrackDTO> topTracks) {
        String key = TOP_TRACKS_KEY_PREFIX + artistId;
        jsonRedisTemplate.opsForValue().set(key, topTracks, 1, TimeUnit.DAYS); // Cache for 1 day, adjust as needed
    }

    public Optional<List<TrackDTO>> getArtistTopTracksFromCache(String artistId) {
        String key = TOP_TRACKS_KEY_PREFIX + artistId;
        return Optional.ofNullable((List<TrackDTO>) jsonRedisTemplate.opsForValue().get(key));
    }
}
