package com.arian.vizpotifybackend.factory;

import com.arian.vizpotifybackend.properties.SpotifyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class SpotifyApiFactory {


    private final SpotifyProperties spotifyProperties;

    public SpotifyApi createSpotifyApiWithAccessToken(String accessToken) {
        return new SpotifyApi.Builder()
                .setAccessToken(accessToken)
                .build();
    }

    public SpotifyApi createSpotifyApiForAuth(){
        URI redirectURI = SpotifyHttpManager.makeUri(spotifyProperties.getRedirectUri());
        return new SpotifyApi.Builder()
                .setClientId(spotifyProperties.getClientId())
                .setClientSecret(spotifyProperties.getClientSecret())
                .setRedirectUri(redirectURI)
                .build();
    }

    public SpotifyApi createSpotifyApiWithClientCredentials(){
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(spotifyProperties.getClientId())
                .setClientSecret(spotifyProperties.getClientSecret())
                .build();
        try{
            final ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
                    .build();
            final CompletableFuture<ClientCredentials> clientCredentialsFuture = clientCredentialsRequest.executeAsync();
            final ClientCredentials clientCredentials = clientCredentialsFuture.join();

            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
            return spotifyApi;
        }catch (Exception e){

        }
        return spotifyApi;
    }

}
