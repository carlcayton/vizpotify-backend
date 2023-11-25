package com.arian.vizpotifybackend.services.user;
import com.arian.vizpotifybackend.dto.AnalyticsDTO;
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

    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    public AnalyticsDTO getAnalyticsForUser(String userId) {
        String url = fastApiBaseUrl + "/analytics/consolidated-analytics/" + userId;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            return new ObjectMapper().readValue(response.getBody(), AnalyticsDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }
}
