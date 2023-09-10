package com.arian.vizpotifybackend.services.user;

import com.arian.vizpotifybackend.Repository.SpotifyAuthTokenRepository;
import com.arian.vizpotifybackend.Repository.UserRepository;
import com.arian.vizpotifybackend.model.SpotifyAuthToken;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.services.auth.spotify.SpotifyOauthTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SpotifyOauthTokenService spotifyOauthtTokenService;
    private final SpotifyAuthTokenRepository spotifyAuthTokenRepository;
    private final UserRepository userRepository;

    @Async
    @Transactional
    public void handleUserRegistration(String userCode) {
        SpotifyApi spotifyApi = null;
        int expiresIn = 0;
        Object[] result= spotifyOauthtTokenService.getApiInstance(userCode).join();
        if (result != null && result.length == 2) {
            spotifyApi = (SpotifyApi) result[0];
            expiresIn = (int) result[1];
        }else{
            throw new RuntimeException("Failed to fetch Spotify userDetail profile.");
        }

        se.michaelthelin.spotify.model_objects.specification.User spotifyUser = getUserProfile(spotifyApi).join();

        UserDetail userDetail = processSpotifyUser(spotifyUser);

        SpotifyAuthToken spotifyAuthToken = spotifyOauthtTokenService.createSpotifyAuthToken(
                userDetail,
                spotifyApi.getAccessToken(),
                spotifyApi.getRefreshToken(),
                expiresIn,
                LocalDateTime.now()
        );
        spotifyAuthTokenRepository.save(spotifyAuthToken);
        System.out.println("USER SAVED");
    }

    private CompletableFuture<se.michaelthelin.spotify.model_objects.specification.User> getUserProfile(SpotifyApi spotifyApi) {
        GetCurrentUsersProfileRequest request = spotifyApi.getCurrentUsersProfile().build();
        return request.executeAsync();
    }

//    @Autowired
//    private JdbcTemplate jdbcTemplate;

    private UserDetail processSpotifyUser(se.michaelthelin.spotify.model_objects.specification.User spotifyUser) {
        UserDetail userDetail = mapSpotifyUserToEntity(spotifyUser);
        Optional<UserDetail> existingUserOpt = userRepository.findBySpotifyId(userDetail.getSpotifyId());
        if (existingUserOpt.isEmpty()) {
            userRepository.save(userDetail);
        }else{
            userDetail.setUpdatedAt(LocalDateTime.now());
            userRepository.save(userDetail);
        }
        return userDetail;
    }

    private UserDetail mapSpotifyUserToEntity(se.michaelthelin.spotify.model_objects.specification.User spotifyUser) {
        return UserDetail.builder()
                .spotifyId(spotifyUser.getId())
                .country(String.valueOf(spotifyUser.getCountry()))
                .displayName(spotifyUser.getDisplayName())
                .externalSpotifyUrl(spotifyUser.getExternalUrls().get("spotify"))
                .followersHref(spotifyUser.getFollowers().getHref())
                .followersTotal(spotifyUser.getFollowers().getTotal())
                .profileHref(spotifyUser.getHref())
                .product(spotifyUser.getProduct()==null? "": spotifyUser.getProduct().getType())
                .profileType(spotifyUser.getType().getType())
                .profileUri(spotifyUser.getUri())
                .isDisplayNamePublic(true) // or any default setting you'd like
                .isProfilePublic(true) // or any default setting you'd like
                .build();
    }
}

