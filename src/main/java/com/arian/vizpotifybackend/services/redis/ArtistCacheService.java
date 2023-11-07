package com.arian.vizpotifybackend.services.redis;

import com.arian.vizpotifybackend.dto.artist.ArtistDTO;
import com.arian.vizpotifybackend.services.artist.ArtistDetailService;
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

    public Optional<ArtistDTO> getArtistDetailsFromCache(String artistId) {
        String key = DETAILS_KEY_PREFIX + artistId;
        return Optional.ofNullable((ArtistDTO) jsonRedisTemplate.opsForValue().get(key));
    }

    public void cacheArtistDetails(String artistId, ArtistDTO artistDetails) {
        String key = DETAILS_KEY_PREFIX + artistId;
        jsonRedisTemplate.opsForValue().set(key, artistDetails, 1, TimeUnit.DAYS); // Adjust cache duration as needed
    }

    public void cacheRelatedArtists(String artistId, List<ArtistDTO> relatedArtists) {
        String key = RELATED_KEY_PREFIX + artistId;
        jsonRedisTemplate.opsForValue().set(key, relatedArtists, 1, TimeUnit.DAYS); // Adjust cache duration as needed
    }

    public Optional<List<ArtistDTO>> getRelatedArtistsFromCache(String artistId) {
        String key = RELATED_KEY_PREFIX + artistId;
        return Optional.ofNullable((List<ArtistDTO>) jsonRedisTemplate.opsForValue().get(key));
    }

    public void incrementArtistAccessCount(String artistId) {
        String key = ACCESS_COUNT_KEY_PREFIX + artistId;
        myStringRedisTemplate.opsForValue().increment(key);
    }

    public Long getArtistAccessCount(String artistId) {
        String key = ACCESS_COUNT_KEY_PREFIX + artistId;
        // Use stringRedisTemplate for counting since it's just a string value
        String value = myStringRedisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0;
    }
}
