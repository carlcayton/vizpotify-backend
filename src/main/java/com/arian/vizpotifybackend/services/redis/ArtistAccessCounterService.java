package com.arian.vizpotifybackend.services.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtistAccessCounterService {


    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_PREFIX = "artist:access:";

    public void incrementArtistAccessCount(String artistId) {
        String key = KEY_PREFIX + artistId;
        redisTemplate.opsForValue().increment(key);
    }

    public Long getArtistAccessCount(String artistId) {
        String key = KEY_PREFIX + artistId;
        return (Long) redisTemplate.opsForValue().get(key);
    }
}
