package com.arian.vizpotifybackend.services.user;

import com.arian.vizpotifybackend.dto.ArtistDTO;
import com.arian.vizpotifybackend.enums.TimeRange;
import com.arian.vizpotifybackend.model.ArtistDetail;
import com.arian.vizpotifybackend.model.UserTopArtist;
import com.arian.vizpotifybackend.repository.UserTopArtistRepository;
import com.arian.vizpotifybackend.services.artist.ArtistDetailService;
import com.arian.vizpotifybackend.services.redis.ArtistCacheService;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import com.arian.vizpotifybackend.services.user.util.TopItemUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTopArtistService {

    private final UserTopArtistRepository userTopArtistRepository;
    private final SpotifyService spotifyService;
    private final ArtistDetailService artistDetailService;

    public Map<String, List<ArtistDTO>> getUserTopArtists(String userSpotifyId) {
        boolean userExists = userTopArtistRepository.existsByUserSpotifyId(userSpotifyId);
        if (userExists) {
            return fetchArtistDetailsForUser(userSpotifyId);
        } else {
            return fetchFromSpotifyAndStoreUserTopArtists(userSpotifyId);
        }
    }
    private Map<String, List<ArtistDTO>> fetchArtistDetailsForUser(String userSpotifyId) {
        Map<String, List<ArtistDTO>> artistDetailsForUser = new HashMap<>();
        List<UserTopArtist> allUserTopArtists = userTopArtistRepository.findByUserSpotifyId(userSpotifyId);

        Map<String, List<String>> timeRangeToArtistIdsMap = allUserTopArtists.stream()
                .collect(Collectors.groupingBy(
                        UserTopArtist::getTimeRange,
                        Collectors.mapping(UserTopArtist::getArtistId, Collectors.toList())
                ));
        System.out.println("test");
        System.out.println(timeRangeToArtistIdsMap);
        System.out.println("test");
        Set<String> allUniqueArtistIds = allUserTopArtists.stream()
                .map(UserTopArtist::getArtistId)
                .collect(Collectors.toSet());
        List<ArtistDetail> allArtistDetails = artistDetailService.getArtistsByIds(new ArrayList<>(allUniqueArtistIds));

        Map<String, ArtistDTO> artistIdToDTOsMap = allArtistDetails.stream()
                .collect(Collectors.toMap(
                        ArtistDetail::getId,
                        artistDetailService::convertArtistDetailToArtistDTO
                ));
        for (String timeRange : TimeRange.getValuesAsList()) {
            List<ArtistDTO> artistDTOsForTimeRange = timeRangeToArtistIdsMap.get(timeRange)
                    .stream()
                    .map(artistIdToDTOsMap::get)
                    .collect(Collectors.toList());

            artistDetailsForUser.put(TopItemUtil.formatTimeRangeForDTO(timeRange), artistDTOsForTimeRange);
        }
        return artistDetailsForUser;
    }


    private Map<String, List<ArtistDTO>> fetchFromSpotifyAndStoreUserTopArtists(String spotifyId) {
        Map<TimeRange, Paging<Artist>> userTopArtistsForAllTimeRange = spotifyService.getUserTopArtistsForAllTimeRange(spotifyId);
        Set<Artist> allArtistsAsSet =
                artistDetailService
                        .extractUniqueArtists(userTopArtistsForAllTimeRange);

        artistDetailService.processAndStoreNewArtistDetails(allArtistsAsSet);

        Map<String, List<ArtistDTO>> output = new HashMap<>();
        for (Map.Entry<TimeRange, Paging<Artist>> entry : userTopArtistsForAllTimeRange.entrySet()) {
            String currentTimeRange = entry.getKey().getValue();
            List<ArtistDTO> artistDTOs = processArtistsForTimeRange(currentTimeRange, spotifyId, entry.getValue());
            output.put(entry.getKey().getValue(), artistDTOs);
        }
        return output;
    }

    private List<ArtistDTO> processArtistsForTimeRange(String timeRange, String spotifyId,Paging<Artist> artistsPage) {
        List<ArtistDTO> artistDTOs = new ArrayList<>();
        List<UserTopArtist> userTopArtists = new ArrayList<>();

        Artist[] artists = artistsPage.getItems();

        int rank = 1;
        for (Artist artist : artists) {
            artistDTOs.add(artistDetailService.convertArtistToArtistDTO(artist));

            UserTopArtist userTopArtist = createUserTopArtist(spotifyId, artist.getId(), timeRange, rank++);
            userTopArtists.add(userTopArtist);
        }
        userTopArtistRepository.saveAll(userTopArtists);  // Save all in one go

        return artistDTOs;
    }

    private UserTopArtist createUserTopArtist(String spotifyId, String artistId, String timeRange, int rank) {
        return UserTopArtist.builder()
                .userSpotifyId(spotifyId)
                .artistId(artistId)
                .timeRange(timeRange)
                .rank(rank)
                .lastUpdated(new Date())
                .build();
    }
}
