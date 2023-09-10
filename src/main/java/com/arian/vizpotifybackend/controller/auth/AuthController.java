package com.arian.vizpotifybackend.controller.auth;

import com.arian.vizpotifybackend.services.auth.spotify.SpotifyOauthTokenService;
import com.arian.vizpotifybackend.services.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SpotifyOauthTokenService spotifyOauthtTokenService;
    private final UserService userService;

    @GetMapping("/login")
    @ResponseBody
    public ResponseEntity<String> getUriForLogin(){
        return ResponseEntity.ok(spotifyOauthtTokenService.getURIRequest());
    }

    @GetMapping(value="/callback/")
    public ResponseEntity<String> registerUser(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        System.out.println("HELLO");
        try {
            response.sendRedirect("http://localhost:3000/dashboard");
            userService.handleUserRegistration(userCode);
            return ResponseEntity.ok("Hello");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
