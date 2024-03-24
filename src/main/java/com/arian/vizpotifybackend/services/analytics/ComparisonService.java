package com.arian.vizpotifybackend.services.analytics;

import com.arian.vizpotifybackend.dto.analytics.ComparisonDTO;
import com.arian.vizpotifybackend.properties.AWSLambdaProperties;
import com.arian.vizpotifybackend.repository.ArtistDetailRepository;
import com.arian.vizpotifybackend.repository.TrackDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComparisonService {

    private final ArtistDetailRepository artistDetailRepository;
    private final TrackDetailRepository trackDetailRepository;
    private final RestTemplate restTemplate;
    private final AWSLambdaProperties awsLambdaProperties;

    public ComparisonDTO getComparison(String user1_id, String user2_id) {
        return requestComparisonFromExternalService(user1_id, user2_id);
    }

    private ComparisonDTO requestComparisonFromExternalService(String user1_spotify_id, String user2_spotify_id) {
        String url = awsLambdaProperties.comparisonEndpoint();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user1_spotify_id ", user1_spotify_id);
        requestBody.put("user2_spotify_id ", user2_spotify_id);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println(response);
            return new ComparisonDTO();
        } catch (Exception e) {
            return null;
        }
    }

}
