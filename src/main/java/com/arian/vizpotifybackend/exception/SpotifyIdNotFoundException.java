package com.arian.vizpotifybackend.exception;


public class SpotifyIdNotFoundException extends UserDetailException {

    public SpotifyIdNotFoundException(String spotifyId) {
        super("No UserDetail found with the given spotifyId: " + spotifyId);
    }
}
