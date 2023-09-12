package com.arian.vizpotifybackend.exception;

public class UserDetailException extends RuntimeException {

    public UserDetailException(String message) {
        super(message);
    }

    public UserDetailException(String message, Throwable cause) {
        super(message, cause);
    }
}

