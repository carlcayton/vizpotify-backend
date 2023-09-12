package com.arian.vizpotifybackend.controller.auth;

import com.arian.vizpotifybackend.model.JwtResponse;
import com.arian.vizpotifybackend.services.auth.spotify.SpotifyOauthTokenService;
import com.arian.vizpotifybackend.services.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SpotifyOauthTokenService spotifyOauthtTokenService;
    private final UserService userService;

    @GetMapping("/login")
    public ResponseEntity<String> getUriForLogin() {
        return ResponseEntity.ok(spotifyOauthtTokenService.getURIRequest());
    }

    @GetMapping(value = "/callback/")
    public ResponseEntity<String> registerUser(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        JwtResponse jwtResponse = userService.handleUserRegistration(userCode);
        Cookie jwtCookie = new Cookie("JWT_TOKEN",jwtResponse.getAccessToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(jwtCookie);
        response.sendRedirect("http://localhost:3000/dashboard");
        return ResponseEntity.ok("User Registered and JWT set in cookie");
    }


}
