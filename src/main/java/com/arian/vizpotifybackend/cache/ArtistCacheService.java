package com.arian.vizpotifybackend.cache;

import com.arian.vizpotifybackend.artist.ArtistDto;
import com.arian.vizpotifybackend.artist.ArtistDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ArtistCacheService {

    private final RedisTemplate<String, String> myStringRedisTemplate;
    private final RedisTemplate<String, Object> jsonRedisTemplate;
    private final ArtistDetailService artistDetailService;

    private static final String ACCESS_COUNT_KEY_PREFIX = "artist:access:";
    private static final String DETAILS_KEY_PREFIX = "artist:details:";
    private static final String RELATED_KEY_PREFIX = "artist:related:";

    public Optional<ArtistDto> getArtistDetailsFromCache(String artistId) {
        String key = DETAILS_KEY_PREFIX + artistId;
        return Optional.ofNullable((ArtistDto) jsonRedisTemplate.opsForValue().get(key));
    }

    public void cacheArtistDetails(String artistId, ArtistDto artistDetails) {
        String key = DETAILS_KEY_PREFIX + artistId;
        jsonRedisTemplate.opsForValue().set(key, artistDetails, 1, TimeUnit.DAYS);
    }

    public void cacheRelatedArtists(String artistId, List<ArtistDto> relatedArtists) {
        String key = RELATED_KEY_PREFIX + artistId;
        jsonRedisTemplate.opsForValue().set(key, relatedArtists, 1, TimeUnit.DAYS);
    }

    public Optional<List<ArtistDto>> getRelatedArtistsFromCache(String artistId) {
        String key = RELATED_KEY_PREFIX + artistId;
        return Optional.ofNullable((List<ArtistDto>) jsonRedisTemplate.opsForValue().get(key));
    }

    public void incrementArtistAccessCount(String artistId) {
        String key = ACCESS_COUNT_KEY_PREFIX + artistId;
        myStringRedisTemplate.opsForValue().increment(key);
    }

    public Long getArtistAccessCount(String artistId) {
        String key = ACCESS_COUNT_KEY_PREFIX + artistId;
        String value = myStringRedisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0;
    }
}
