package com.arian.vizpotifybackend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SpotifyIdNotFoundException extends RuntimeException {
    private String spotifyId;

    public SpotifyIdNotFoundException(String spotifyId) {
        super("User not found with Spotify ID: " + spotifyId);
        this.spotifyId = spotifyId;
    }

    public String getSpotifyId() {
        return spotifyId;
    }
}