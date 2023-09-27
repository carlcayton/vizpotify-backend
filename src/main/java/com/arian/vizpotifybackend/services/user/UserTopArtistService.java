package com.arian.vizpotifybackend.services.user;

import com.arian.vizpotifybackend.dto.artist.ArtistDTO;
import com.arian.vizpotifybackend.enums.TimeRange;
import com.arian.vizpotifybackend.model.ArtistDetail;
import com.arian.vizpotifybackend.model.UserTopArtist;
import com.arian.vizpotifybackend.repository.UserTopArtistRepository;
import com.arian.vizpotifybackend.services.artist.ArtistDetailService;
import com.arian.vizpotifybackend.services.artist.CommonArtistService;
import com.arian.vizpotifybackend.services.redis.ArtistAccessCounterService;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
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
    private final ArtistAccessCounterService counterService;
    private final CommonArtistService commonArtistService;

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

        // 1. Get a consolidated set of all unique artist IDs across all time ranges.
        Map<String, Set<String>> timeRangeToArtistIdsMap = new HashMap<>();
        Set<String> allUniqueArtistIds = new HashSet<>();

        for (String timeRange : TimeRange.getValuesAsList()) {
            List<UserTopArtist> userTopArtists = userTopArtistRepository.findByUserSpotifyIdAndTimeRange(userSpotifyId, timeRange);

            Set<String> artistIdsForTimeRange = userTopArtists
                    .stream()
                    .map(UserTopArtist::getArtistId)
                    .collect(Collectors.toSet());

            timeRangeToArtistIdsMap.put(timeRange, artistIdsForTimeRange);
            allUniqueArtistIds.addAll(artistIdsForTimeRange);
        }
        allUniqueArtistIds.forEach(counterService::incrementArtistAccessCount);


        // 2. Fetch ArtistDetail objects for these unique artist IDs.
        List<ArtistDetail> allArtistDetails = artistDetailService.getArtistsByIds(new ArrayList<>(allUniqueArtistIds));
        Map<String, ArtistDTO> artistIdToDTOsMap = allArtistDetails.stream()
                .collect(Collectors.toMap(
                        ArtistDetail::getId,
                        artistDetailService::convertArtistDetailToArtistDTO
                ));

        // 3. Loop through each time range again and map the already fetched ArtistDetail objects back to each time range.
        for (String timeRange : TimeRange.getValuesAsList()) {
            List<ArtistDTO> artistDTOsForTimeRange = timeRangeToArtistIdsMap.get(timeRange)
                    .stream()
                    .map(artistIdToDTOsMap::get)
                    .collect(Collectors.toList());

            artistDetailsForUser.put(formatTimeRangeForDTO(timeRange), artistDTOsForTimeRange);
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
            // Convert artist to DTO and add to the list
            artistDTOs.add(artistDetailService.convertArtistToArtistDTO(artist));

            // Store UserTopArtist in the list
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
    private String formatTimeRangeForDTO(String timeRange) {
        switch (timeRange) {
            case "short_term":
                return "shortTerm";
            case "medium_term":
                return "mediumTerm";
            case "long_term":
                return "longTerm";
            default:
                return "";
        }
    }

}
