package com.arian.vizpotifybackend.services.auth.spotify;


import com.arian.vizpotifybackend.factory.SpotifyApiFactory;
import com.arian.vizpotifybackend.model.SpotifyAuthToken;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.properties.SpotifyProperties;
import com.arian.vizpotifybackend.repository.SpotifyAuthTokenRepository;
import com.arian.vizpotifybackend.util.SpotifyUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
public class SpotifyOauthTokenService {

    private final SpotifyUtil spotifyUtil;
    private final SpotifyProperties spotifyProperties;
    private final SpotifyApiFactory spotifyApiFactory;
    private final SpotifyAuthTokenRepository spotifyAuthTokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(SpotifyOauthTokenService.class);


    public String getURIRequest() {
        SpotifyApi spotifyApi = spotifyApiFactory.createSpotifyApiForAuth();
        String scopeAsCSV = spotifyUtil.convertScopeListToCSV(spotifyProperties.getScopes());
        AuthorizationCodeUriRequest authorizationCodeRequest = spotifyApi.authorizationCodeUri()
                .scope(scopeAsCSV)
                .show_dialog(true)
                .build();

        try {
            return authorizationCodeRequest.executeAsync()
                    .thenApply(URI::toString)
                    .join();
        } catch (CompletionException ex) {
            logger.error("Error: {}", ex.getCause().getMessage());
            return null;
        }
    }



    public Object[] getApiInstance(String userCode) {
        SpotifyApi spotifyApi = spotifyApiFactory.createSpotifyApiForAuth();
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(userCode).build();

        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            return new Object[]{spotifyApi, authorizationCodeCredentials.getExpiresIn()};
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return null;
        }
    }

    public SpotifyAuthToken createSpotifyAuthToken(String userSpotifyId, String accessToken,
                                                   String refreshToken,
                                                   Integer expiresIn,
                                                   LocalDateTime lastUpdated) {

        return SpotifyAuthToken.builder()
                .userSpotifyId(userSpotifyId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .lastUpdated(lastUpdated)
                .build();
    }
    public void save(SpotifyAuthToken spotifyAuthToken){
        spotifyAuthTokenRepository.save(spotifyAuthToken);
    }
}
