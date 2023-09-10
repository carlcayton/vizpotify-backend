package com.arian.vizpotifybackend.controllers.auth;

import com.arian.vizpotifybackend.properties.SpotifyProperties;
import com.arian.vizpotifybackend.services.auth.spotify.AuthTokenService;
import com.arian.vizpotifybackend.services.auth.spotify.AuthURIService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthURIService authURIService;
    private final AuthTokenService authTokenService;

    @GetMapping("/login")
    @ResponseBody
    public CompletableFuture<ResponseEntity<String>> spotifyLogin(){
        return authURIService.getURIRequest()
                .thenApply(uri->ResponseEntity.ok(uri))
                .exceptionally(ex->ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error occurred: "+ ex.getMessage()));
    }

    @GetMapping(value="/get-user-code/")
    public CompletableFuture<ResponseEntity<SpotifyApi>> getSpotifyUserCode(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:3000/dashboard");

        return authTokenService.getApiInstance(userCode)
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }
}
