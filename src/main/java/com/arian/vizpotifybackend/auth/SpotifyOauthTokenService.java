package com.arian.vizpotifybackend.auth;

import com.arian.vizpotifybackend.common.config.SpotifyConfig;
import com.arian.vizpotifybackend.common.properties.SpotifyProperties;
import com.arian.vizpotifybackend.common.util.SpotifyUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetAudioFeaturesForSeveralTracksRequest;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@RequiredArgsConstructor
public class SpotifyOauthTokenService {

    private final SpotifyUtil spotifyUtil;
    private final SpotifyProperties spotifyProperties;
    private final SpotifyAuthTokenRepository spotifyAuthTokenRepository;
    private final SpotifyConfig spotifyConfig;

    private static final Logger logger = LoggerFactory.getLogger(SpotifyOauthTokenService.class);

    public String getURIRequest() {
        SpotifyApi spotifyApi = spotifyConfig.spotifyApiForAuth();
        String scopeAsCSV = spotifyUtil.convertScopeListToCSV(spotifyProperties.getScopes());
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope(scopeAsCSV)
                .show_dialog(true)
                .build();

        try {
            URI uri = authorizationCodeUriRequest.execute();
            return uri.toString();
        } catch (Exception e) {
            logger.error("Error getting URI request: {}", e.getMessage());
            return null;
        }
    }

    public AudioFeatures[] getAudioFeaturesForSeveralTracks(List<String> ids) {
        SpotifyApi spotifyApi = spotifyConfig.spotifyApiWithClientCredentials();
        try {
            GetAudioFeaturesForSeveralTracksRequest getAudioFeaturesForSeveralTracksRequest =
                    spotifyApi.getAudioFeaturesForSeveralTracks(spotifyUtil.listToCsv(ids))
                            .build();
            final CompletableFuture<AudioFeatures[]> audioFeaturesFuture = getAudioFeaturesForSeveralTracksRequest.executeAsync();
            return audioFeaturesFuture.join();
        } catch (CompletionException e) {
            logger.error("Error getting audio features: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error getting audio features: {}", e.getMessage());
        }
        return null;
    }

    public Object[] getApiInstance(String userCode) {
        SpotifyApi spotifyApi = spotifyConfig.spotifyApiForAuth();
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(userCode).build();

        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
            return new Object[]{spotifyApi, authorizationCodeCredentials.getExpiresIn()};
        } catch (Exception ex) {
            logger.error("Error getting API instance: {}", ex.getMessage());
            return new Object[]{null, null};
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

    public void save(SpotifyAuthToken spotifyAuthToken) {
        spotifyAuthTokenRepository.save(spotifyAuthToken);
    }
}