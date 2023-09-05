package com.arian.vizpotifybackend.services.auth.spotify;

import com.arian.vizpotifybackend.factories.SpotifyApiFactory;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final SpotifyApiFactory spotifyApiFactory;

    public CompletableFuture<SpotifyApi> getApiInstance(String userCode) {
        SpotifyApi spotifyApi = spotifyApiFactory.createSpotifyApiForAuth();
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(userCode).build();

        return authorizationCodeRequest.executeAsync().thenApply(authorizationCodeCredentials -> {
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            return spotifyApi;
        }).exceptionally(ex -> {
            System.out.println("Error: " + ex.getCause().getMessage());
            return null;
        });
    }


}
