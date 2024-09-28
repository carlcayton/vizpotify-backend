
package com.arian.vizpotifybackend.common.config;

import com.arian.vizpotifybackend.common.properties.SpotifyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.net.URI;

@Configuration
public class SpotifyConfig {

    private final SpotifyProperties spotifyProperties;

    public SpotifyConfig(SpotifyProperties spotifyProperties) {
        this.spotifyProperties = spotifyProperties;
    }

    @Bean
    public SpotifyApi spotifyApiForAuth() {
        URI redirectURI = SpotifyHttpManager.makeUri(spotifyProperties.getRedirectUri());
        return new SpotifyApi.Builder()
                .setClientId(spotifyProperties.getClientId())
                .setClientSecret(spotifyProperties.getClientSecret())
                .setRedirectUri(redirectURI)
                .build();
    }

    @Bean
    public SpotifyApi spotifyApiWithClientCredentials() {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(spotifyProperties.getClientId())
                .setClientSecret(spotifyProperties.getClientSecret())
                .build();

        try {
            ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();
            ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
        } catch (Exception e) {
            // Log the exception or handle it appropriately
        }

        return spotifyApi;
    }

    public SpotifyApi spotifyApiWithAccessToken(String accessToken) {
        return new SpotifyApi.Builder()
                .setAccessToken(accessToken)
                .build();
    }
}