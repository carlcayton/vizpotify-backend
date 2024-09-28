package com.arian.vizpotifybackend.user.topitems.artist;

import com.arian.vizpotifybackend.artist.ArtistDto;
import com.arian.vizpotifybackend.artist.ArtistDetail;
import com.arian.vizpotifybackend.artist.ArtistDetailService;
import com.arian.vizpotifybackend.common.SpotifyService;
import com.arian.vizpotifybackend.common.TimeRange;
import com.arian.vizpotifybackend.common.mapper.ArtistMapper;

import com.arian.vizpotifybackend.user.topitems.common.TopItemUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTopArtistService {

    private final UserTopArtistRepository userTopArtistRepository;
    private final SpotifyService spotifyService;
    private final ArtistDetailService artistDetailService;
    private final ArtistMapper artistMapper;

    public Map<String, List<ArtistDto>> getUserTopArtists(String userSpotifyId) {
        boolean userExists = userTopArtistRepository.existsByUserSpotifyId(userSpotifyId);
        if (userExists) {
            return fetchUserTopItemsFromDB(userSpotifyId);
        } else {
            return fetchFromSpotifyAndStoreUserTopArtists(userSpotifyId);
        }
    }
    public Map<String, List<ArtistDto>> fetchUserTopItemsFromDB(String userId) {
        Map<String, List<ArtistDto>> artistDetailsForUser = new HashMap<>();
        List<UserTopArtist> allUserTopArtists = userTopArtistRepository.findByUserSpotifyId(userId);

        List<String> artistIds = allUserTopArtists.stream()
                .map(UserTopArtist::getArtistId)
                .collect(Collectors.toList());

        List<ArtistDetail> artistDetailsList = artistDetailService.getArtistsByIds(artistIds);

        Map<String, ArtistDetail> artistIdToDetailMap = artistDetailsList.stream()
                .collect(Collectors.toMap(ArtistDetail::getId, Function.identity()));

        for (UserTopArtist userTopArtist : allUserTopArtists) {
            ArtistDetail artistDetail = artistIdToDetailMap.get(userTopArtist.getArtistId());
            if (artistDetail != null) {
                ArtistDto artistDto = artistMapper.artistDetailToArtistDto(artistDetail);
                String userArtistTimeRange = TopItemUtil.formatTimeRangeForDto(userTopArtist.getTimeRange());
                artistDetailsForUser.computeIfAbsent(userArtistTimeRange, k -> new ArrayList<>()).add(artistDto);
            }
        }

        return artistDetailsForUser;
    }

    private Map<String, List<ArtistDto>> fetchFromSpotifyAndStoreUserTopArtists(String spotifyId) {
        Map<TimeRange, Paging<Artist>> userTopArtistsForAllTimeRange = spotifyService.getUserTopArtistsForAllTimeRange(spotifyId);
        Set<Artist> allArtistsAsSet =
                artistDetailService
                        .extractUniqueArtists(userTopArtistsForAllTimeRange);

        artistDetailService.processAndStoreNewArtistDetails(allArtistsAsSet);

        Map<String, List<ArtistDto>> output = new HashMap<>();
        for (Map.Entry<TimeRange, Paging<Artist>> entry : userTopArtistsForAllTimeRange.entrySet()) {
            String currentTimeRange = entry.getKey().getValue();
            List<ArtistDto> artistDtos = processArtistsForTimeRange(currentTimeRange, spotifyId, entry.getValue());
            output.put(TopItemUtil.formatTimeRangeForDto(currentTimeRange), artistDtos);
        }
        return output;
    }

    private List<ArtistDto> processArtistsForTimeRange(String timeRange, String spotifyId,Paging<Artist> artistsPage) {
        List<ArtistDto> artistDtos = new ArrayList<>();
        List<UserTopArtist> userTopArtists = new ArrayList<>();

        Artist[] artists = artistsPage.getItems();

        int rank = 1;
        for (Artist artist : artists) {
            artistDtos.add(artistMapper.artistToArtistDto(artist));

            UserTopArtist userTopArtist = createUserTopArtist(spotifyId, artist.getId(), timeRange, rank++);
            userTopArtists.add(userTopArtist);
        }
        userTopArtistRepository.saveAll(userTopArtists);

        return artistDtos;
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
