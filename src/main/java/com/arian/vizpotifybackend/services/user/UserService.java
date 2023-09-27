package com.arian.vizpotifybackend.services.user;

import com.arian.vizpotifybackend.exception.SpotifyIdNotFoundException;
import com.arian.vizpotifybackend.model.JwtResponse;
import com.arian.vizpotifybackend.repository.SpotifyAuthTokenRepository;
import com.arian.vizpotifybackend.repository.UserDetailRepository;
import com.arian.vizpotifybackend.model.SpotifyAuthToken;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.services.auth.jwt.JwtService;
import com.arian.vizpotifybackend.services.auth.spotify.SpotifyOauthTokenService;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import jakarta.transaction.Transactional;
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

    private final SpotifyOauthTokenService spotifyOauthTokenService;
    private final JwtService jwtService;
    private final SpotifyService spotifyService;


    @Transactional
    public JwtResponse handleUserRegistration(String userCode) {
        SpotifyApi spotifyApi = null;
        int expiresIn = 0;
        Object[] result= spotifyOauthTokenService.getApiInstance(userCode).join();
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
        return new JwtResponse(accessToken);
    }
    public UserDetail lodUserDetailBySpotifyId(String spotifyId){
        Optional<UserDetail> optionalUserDetail = userDetailRepository.findBySpotifyId(spotifyId);
        if(optionalUserDetail.isPresent()){
            return optionalUserDetail.get();
        }else{
            throw new SpotifyIdNotFoundException(spotifyId);
        }
    }


    private UserDetail processSpotifyUser(User spotifyUser) {
        UserDetail userDetail = mapSpotifyUserToEntity(spotifyUser);
        Optional<UserDetail> existingUserOpt = userDetailRepository.findBySpotifyId(userDetail.getSpotifyId());
        if (existingUserOpt.isEmpty()) {
            userDetail.setCreatedAt(LocalDateTime.now());
            userDetailRepository.save(userDetail);
        }

        return userDetail;
    }

    private UserDetail mapSpotifyUserToEntity(User spotifyUser) {
        return UserDetail.builder()
                .spotifyId(spotifyUser.getId())
                .email(spotifyUser.getEmail())
                .country(String.valueOf(spotifyUser.getCountry()))
                .displayName(spotifyUser.getDisplayName())
                .externalSpotifyUrl(spotifyUser.getExternalUrls().get("spotify"))
                .followersHref(spotifyUser.getFollowers().getHref())
                .followersTotal(spotifyUser.getFollowers().getTotal())
                .profileHref(spotifyUser.getHref())
                .product(spotifyUser.getProduct()==null? "": spotifyUser.getProduct().getType())
                .profileType(spotifyUser.getType().getType())
                .profilePictureUrl(spotifyUser.getImages().length>0? spotifyUser.getImages()[0].getUrl():"")
                .profileUri(spotifyUser.getUri())
                .isDisplayNamePublic(true)
                .isProfilePublic(true)
                .build();
    }
}

