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
import java.util.function.Function;
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
    private final ArtistCacheService artistCacheService;

    public Map<String, List<ArtistDTO>> getUserTopArtists(String userSpotifyId) {
        boolean userExists = userTopArtistRepository.existsByUserSpotifyId(userSpotifyId);
        if (userExists) {
            return fetchUserTopItemsFromDB(userSpotifyId);
        } else {
            return fetchFromSpotifyAndStoreUserTopArtists(userSpotifyId);
        }
    }
    public Map<String, List<ArtistDTO>> fetchUserTopItemsFromDB(String userId) {
        Map<String, List<ArtistDTO>> artistDetailsForUser = new HashMap<>();
        List<UserTopArtist> allUserTopArtists = userTopArtistRepository.findByUserSpotifyId(userId);

        // Extract all artist IDs associated with the user's top artists
        List<String> artistIds = allUserTopArtists.stream()
                .map(UserTopArtist::getArtistId)
                .collect(Collectors.toList());

        // Retrieve artist details for all artist IDs collected from the user's top artists
        List<ArtistDetail> artistDetailsList = artistDetailService.getArtistsByIds(artistIds);

        // Map each ArtistDetail to its ID for quick access during DTO conversion
        Map<String, ArtistDetail> artistIdToDetailMap = artistDetailsList.stream()
                .collect(Collectors.toMap(ArtistDetail::getId, Function.identity()));

        // Build a DTO list for each time range category
        for (UserTopArtist userTopArtist : allUserTopArtists) {
            ArtistDetail artistDetail = artistIdToDetailMap.get(userTopArtist.getArtistId());
            if (artistDetail != null) {
                ArtistDTO artistDTO = artistDetailService.convertArtistDetailToArtistDTO(artistDetail);
                String userArtistTimeRange = TopItemUtil.formatTimeRangeForDTO(userTopArtist.getTimeRange());
                artistDetailsForUser.computeIfAbsent(userArtistTimeRange, k -> new ArrayList<>()).add(artistDTO);
            }
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
            output.put(TopItemUtil.formatTimeRangeForDTO(currentTimeRange), artistDTOs);
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
        userTopArtistRepository.saveAll(userTopArtists);

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
