package com.arian.vizpotifybackend.services.artist;


import com.arian.vizpotifybackend.dto.artist.ArtistDTO;
import com.arian.vizpotifybackend.model.ArtistDetail;
import com.arian.vizpotifybackend.model.Genre;
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
    private final RelatedArtistService relatedArtistService;

    @Transactional
    public List<ArtistDTO> processAndStoreNewArtistDetails(Map<String, Paging<Artist>> userTopArtistsForAllTimeRange) {

        Set<Artist> allUniqueArtists = extractUniqueArtists(userTopArtistsForAllTimeRange);
        List<Artist> artistsNotInTable = commonArtistService.extractArtistNotInArtistTable(allUniqueArtists);

        List<ArtistDetail> artistDetails = artistsNotInTable.stream()
                .map(commonArtistService::convertArtistToArtistDetail)
                .collect(Collectors.toList());

        genreService.saveNewGenresGivenArtists(artistDetails);
        artistDetailRepository.saveAll(artistDetails);

        return artistDetails.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private Set<Artist> extractUniqueArtists(Map<String, Paging<Artist>> artistPagingMap) {
        Set<Artist> allUniqueArtists = new HashSet<>();
        for (Map.Entry<String, Paging<Artist>> entry : artistPagingMap.entrySet()) {
            Artist[] artists = entry.getValue().getItems();
            allUniqueArtists.addAll(Arrays.asList(artists));
        }
        return allUniqueArtists;
    }


    public ArtistDTO convertToDTO(ArtistDetail artistDetail) {
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



}
