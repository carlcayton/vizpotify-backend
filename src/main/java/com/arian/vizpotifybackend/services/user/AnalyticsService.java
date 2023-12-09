package com.arian.vizpotifybackend.services.user;
import com.arian.vizpotifybackend.dto.AnalyticsDTO;
import com.arian.vizpotifybackend.repository.UserTopArtistRepository;
import com.arian.vizpotifybackend.repository.UserTopTrackRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;


@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final RestTemplate restTemplate;
    private final UserTopArtistRepository userTopArtistRepository;
    private final UserTopTrackRepository userTopTrackRepository;

    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    public AnalyticsDTO getAnalyticsForUser(String userId) {
        int attempts = 0;
        final int maxAttempts = 5;

        while (true) {
            boolean userTopArtistExists = userTopArtistRepository.existsByUserSpotifyId(userId);
            boolean userTopTrackExists = userTopTrackRepository.existsByUserSpotifyId(userId);

            if (userTopArtistExists && userTopTrackExists) {
                try {
                    String url = fastApiBaseUrl + "/analytics/consolidated-analytics/" + userId;
                    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                    return new ObjectMapper().readValue(response.getBody(), AnalyticsDTO.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Error processing JSON", e);
                }
            } else {
                attempts++;
                if (attempts >= maxAttempts) {
                    throw new IllegalStateException("UserTopArtist or UserTopTrack data not available for user: " + userId);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted", e);
                }
            }
        }
    }


}
