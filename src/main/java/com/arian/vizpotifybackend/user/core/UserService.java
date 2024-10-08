
package com.arian.vizpotifybackend.user.core;

import com.arian.vizpotifybackend.auth.JwtResponse;
import com.arian.vizpotifybackend.auth.JwtService;
import com.arian.vizpotifybackend.auth.SpotifyOauthTokenService;
import com.arian.vizpotifybackend.auth.SpotifyAuthToken;
import com.arian.vizpotifybackend.common.SpotifyService;
import com.arian.vizpotifybackend.common.exception.SpotifyIdNotFoundException;
import com.arian.vizpotifybackend.common.mapper.UserMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDetailRepository userDetailRepository;
    private final SpotifyService spotifyService;
    private final UserMapper userMapper;
    private final SpotifyOauthTokenService spotifyOauthTokenService;
    private final JwtService jwtService;
    @Transactional
    public JwtResponse handleUserRegistration(String userCode) {
        SpotifyApi spotifyApi;
        int expiresIn;
        Object[] result= spotifyOauthTokenService.getApiInstance(userCode);
        if (result != null && result.length == 2) {
            spotifyApi = (SpotifyApi) result[0];
            expiresIn = (int) result[1];
        }else{
            throw new RuntimeException("Failed to fetch Spotify userDetail profile.");
        }
        User spotifyUser = spotifyService.getUserProfile(spotifyApi);
        UserDetail userDetail = processSpotifyUser(spotifyUser);
        SpotifyAuthToken spotifyAuthToken = spotifyOauthTokenService.createSpotifyAuthToken(
                userDetail.getSpotifyId(),
                spotifyApi.getAccessToken(),
                spotifyApi.getRefreshToken(),
                expiresIn,
                LocalDateTime.now()
        );

        spotifyOauthTokenService.save(spotifyAuthToken);
        String accessToken = jwtService.createToken(userDetail);
        return new JwtResponse(accessToken, userDetail.getSpotifyId());
    }
    public UserDetail loadUserDetailBySpotifyId(String spotifyId){
        Optional<UserDetail> optionalUserDetail = userDetailRepository.findBySpotifyId(spotifyId);
        if(optionalUserDetail.isPresent()){
            return optionalUserDetail.get();
        }else{
            throw new SpotifyIdNotFoundException(spotifyId);
        }
    }

    private UserDetail processSpotifyUser(User spotifyUser) {
        UserDetail userDetail = userMapper.userDetailToUser(spotifyUser);
        Optional<UserDetail> existingUserOpt = userDetailRepository.findBySpotifyId(userDetail.getSpotifyId());
        if (existingUserOpt.isEmpty()) {
            userDetail.setCreatedAt(LocalDateTime.now());
            userDetail.setUpdatedAt(LocalDateTime.now());
            userDetailRepository.save(userDetail);
        }

        return userDetail;
    }

    public boolean isAnalyticsAvailable(String userId) {
        return userDetailRepository.findAnalyticsAvailableBySpotifyId(userId);
    }
    public void setAnalyticsAvailable(String userId, boolean available) {
        userDetailRepository.findBySpotifyId(userId)
                .ifPresent(userDetail -> {
                    userDetail.setAnalyticsAvailable(available);
                    userDetailRepository.save(userDetail);
                });
    }
    public Optional<UserDetail> findBySpotifyId(String spotifyId) {
        return userDetailRepository.findBySpotifyId(spotifyId);
    }
    public void save(UserDetail user) {
        userDetailRepository.save(user);
    }
}

