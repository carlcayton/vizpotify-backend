package com.arian.vizpotifybackend.services.auth.spotify;


import com.arian.vizpotifybackend.factories.SpotifyApiFactory;
import com.arian.vizpotifybackend.properties.SpotifyProperties;
import com.arian.vizpotifybackend.util.SpotifyUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
public class AuthURIService {

    private final SpotifyUtil spotifyUtil;
    private final SpotifyProperties spotifyProperties;
    private final SpotifyApiFactory spotifyApiFactory;
    private static final Logger logger = LoggerFactory.getLogger(AuthURIService.class);


    public CompletableFuture<String> getURIRequest() {
        SpotifyApi spotifyApi = spotifyApiFactory.createSpotifyApiForAuth();
        String scopeAsCSV = spotifyUtil.convertScopeListToCSV(spotifyProperties.getScopes());
        AuthorizationCodeUriRequest authorizationCodeRequest = spotifyApi.authorizationCodeUri()
                .scope(scopeAsCSV)
                .show_dialog(true)
                .build();

        return authorizationCodeRequest.executeAsync()
                .thenApply(URI::toString)
                .exceptionally(ex -> {
                    logger.error("Error: {}", ex.getMessage());
                    return null;
                });
    }

}
