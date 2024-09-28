package com.arian.vizpotifybackend.auth;

import com.arian.vizpotifybackend.user.core.UserDetail;
import com.arian.vizpotifybackend.user.core.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserService userService;



    public String extractJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    public Map<String, Object> getUserAuthenticationStatus(HttpServletRequest request) {
        String token = extractJwtFromCookie(request);
        if (token != null && !jwtService.isTokenExpired(token)) {
            try {
                String spotifyId = jwtService.extractSpotifyId(token);
                UserDetail userDetail = userService.loadUserDetailBySpotifyId(spotifyId);
                return Map.of(
                        "isAuthenticated", true,
                        "spotifyId", spotifyId,
                        "userDisplayName", userDetail.getDisplayName(),
                        "profilePictureUrl", userDetail.getProfilePictureUrl());
            } catch (Exception e) {
                // Log exception or handle it as needed
            }
        }
        return Map.of("isAuthenticated", false);
    }
}
