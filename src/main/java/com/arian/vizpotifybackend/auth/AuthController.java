package com.arian.vizpotifybackend.auth;

import com.arian.vizpotifybackend.user.core.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {


    private final SpotifyOauthTokenService spotifyOauthTokenService;
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<String> getUriForLogin() {
        return ResponseEntity.ok(spotifyOauthTokenService.getURIRequest());
    }

    @GetMapping("/callback/")
    public ResponseEntity<String> registerUser(@RequestParam("code") String userCode, HttpServletResponse response) throws IOException {
        JwtResponse jwtResponse = userService.handleUserRegistration(userCode);
        Cookie jwtCookie = new Cookie("JWT_TOKEN", jwtResponse.getAccessToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(jwtCookie);

        response.sendRedirect("http://localhost:3000/dashboard/"+jwtResponse.getSpotifyId());
        return ResponseEntity.ok("User Registered and JWT set in cookie");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("JWT_TOKEN", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        return ResponseEntity.ok("User logged out successfully");
    }

    @GetMapping("/status")
    public ResponseEntity<Object> isAuthenticated(HttpServletRequest request) {
        Map<String, Object> authStatus = authService.getUserAuthenticationStatus(request);
        return ResponseEntity.ok(authStatus);
    }
}
