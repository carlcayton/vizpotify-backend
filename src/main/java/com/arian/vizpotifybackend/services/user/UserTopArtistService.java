package com.arian.vizpotifybackend.services.user;

import com.arian.vizpotifybackend.dto.artist.ArtistDTO;
import com.arian.vizpotifybackend.repository.UserTopArtistRepository;
import com.arian.vizpotifybackend.services.artist.ArtistDetailService;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserTopArtistService {

    private final UserTopArtistRepository userTopArtistRepository;
    private final SpotifyService spotifyService;
    private final ArtistDetailService artistDetailService;


    public List<ArtistDTO> getUserTopArtists(String spotifyId){
       boolean userExists = userTopArtistRepository.existsByUserDetailSpotifyId(spotifyId);
       if(userExists){
           System.out.println("");
       }else{
            fetchAndStoreUserTopArtists(spotifyId);
       }
       return null;
    }



    public List<ArtistDTO> fetchAndStoreUserTopArtists(String spotifyId){

        Map<String, Paging<Artist>> userTopArtistsForAllTimeRange= spotifyService.getUserTopArtistsForAllTimeRange(spotifyId);

        return artistDetailService.processAndStoreNewArtistDetails(userTopArtistsForAllTimeRange);
    }









}
