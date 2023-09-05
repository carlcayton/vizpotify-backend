package com.arian.vizpotifybackend.factories;

import com.arian.vizpotifybackend.properties.SpotifyProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

import java.net.URI;

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


}
