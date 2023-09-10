package com.arian.vizpotifybackend.services.auth.jwt;

import com.arian.vizpotifybackend.Repository.SpotifyAuthTokenRepository;
import com.arian.vizpotifybackend.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JWTAuthService {

    @Autowired
    private SpotifyAuthTokenRepository authTokenRepository;

    private final JwtService jwtService;

//    public TokenResponse handleUserRegistration(String userCode) {
//        // ... previous code to obtain and save Spotify tokens
//
//        // Create JWT token
//        String jwtToken = jwtService.createToken(userDetail);
//
//        return new TokenResponse(jwtToken);
//    }
}
