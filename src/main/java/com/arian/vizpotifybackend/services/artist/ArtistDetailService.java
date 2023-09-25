package com.arian.vizpotifybackend.services.artist;


import com.arian.vizpotifybackend.dto.artist.ArtistDTO;
import com.arian.vizpotifybackend.enums.TimeRange;
import com.arian.vizpotifybackend.model.ArtistDetail;
import com.arian.vizpotifybackend.model.Genre;
import com.arian.vizpotifybackend.model.UserTopArtist;
import com.arian.vizpotifybackend.repository.ArtistDetailRepository;
import com.arian.vizpotifybackend.services.GenreService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Paging;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistDetailService {

    private final ArtistDetailRepository artistDetailRepository;
    private final GenreService genreService;
    private final CommonArtistService commonArtistService;

    @Transactional
    public void processAndStoreNewArtistDetails(Map<TimeRange, Paging<Artist>> userTopArtistsForAllTimeRange) {

        Set<Artist> allUniqueArtists = extractUniqueArtists(userTopArtistsForAllTimeRange);
        List<Artist> artistsNotInTable = commonArtistService.extractArtistNotInArtistTable(allUniqueArtists);

        List<ArtistDetail> artistDetails = artistsNotInTable.stream()
                .map(commonArtistService::convertArtistToArtistDetail)
                .collect(Collectors.toList());

        genreService.saveNewGenresGivenArtists(artistDetails);
        artistDetailRepository.saveAll(artistDetails);
    }
    public ArtistDetail findById(String artistId){
        return artistDetailRepository.findById(artistId).orElse(null);
    }

    private Set<Artist> extractUniqueArtists(Map<TimeRange, Paging<Artist>> artistPagingMap) {
        Set<Artist> allUniqueArtists = new HashSet<>();
        for (Map.Entry<TimeRange, Paging<Artist>> entry : artistPagingMap.entrySet()) {
            Artist[] artists = entry.getValue().getItems();
            allUniqueArtists.addAll(Arrays.asList(artists));
        }
        return allUniqueArtists;
    }


    public ArtistDTO convertArtistDetailToArtistDTO(ArtistDetail artistDetail) {
        List<String> genreNames = artistDetail.getGenres().stream()
                .map(Genre::getName)  // Assuming Genre has a getName() method.
                .collect(Collectors.toList());

        return ArtistDTO.builder()
                .id(artistDetail.getId())
                .followersTotal(artistDetail.getFollowersTotal())
                .name(artistDetail.getName())
                .popularity(artistDetail.getPopularity())
                .externalUrl(artistDetail.getExternalUrl())
                .imageUrl(artistDetail.getImageUrl())
                .genres(genreNames)
                .build();
    }

   public ArtistDTO convertArtistToArtistDTO(Artist artist) {
        return convertArtistDetailToArtistDTO(commonArtistService.convertArtistToArtistDetail(artist));
    }

    public List<ArtistDetail> getArtistsByIds(List<String> ids) {
        return artistDetailRepository.findByIdIn(ids);
    }
}
