package com.arian.vizpotifybackend.services.redis;

import com.arian.vizpotifybackend.dto.artist.ArtistDTO;
import com.arian.vizpotifybackend.services.artist.ArtistDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ArtistCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ArtistDetailService artistDetailService; // Assuming this is where you fetch artist details

    private static final String KEY_PREFIX = "artist:details:";

    public Optional<ArtistDTO> getArtistDetailsFromCache(String artistId) {
        String key = KEY_PREFIX + artistId;
        return Optional.ofNullable((ArtistDTO) redisTemplate.opsForValue().get(key));
    }

    public void cacheArtistDetails(String artistId, ArtistDTO artistDetails) {
        String key = KEY_PREFIX + artistId;
        redisTemplate.opsForValue().set(key, artistDetails, 1, TimeUnit.DAYS); // Adjust cache duration as needed
    }

//    public ArtistDTO fetchAndCacheArtistDetails(String artistId) {
//        ArtistDTO artistDetails = artistDetailService.getArtistById(artistId); // Your method to fetch artist details
//        cacheArtistDetails(artistId, artistDetails);
//        return artistDetails;
//    }
}
