package com.arian.vizpotifybackend.services.artist;


import com.arian.vizpotifybackend.dto.ArtistDTO;
import com.arian.vizpotifybackend.enums.TimeRange;
import com.arian.vizpotifybackend.model.ArtistDetail;
import com.arian.vizpotifybackend.repository.ArtistDetailRepository;
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
    private final CommonArtistService commonArtistService;

    @Transactional
    public void processAndStoreNewArtistDetails(Set<Artist> allArtistDetails) {
        Set<Artist> artistsNotInTable = commonArtistService.extractArtistNotInArtistTable(allArtistDetails);
        Set<ArtistDetail> newArtists= artistsNotInTable.stream()
                .map(commonArtistService::convertArtistToArtistDetail)
                .collect(Collectors.toSet());

        artistDetailRepository.saveAll(newArtists);
    }

    public ArtistDetail getArtistById(String artistId){
        return artistDetailRepository.findById(artistId).orElse(null);
    }

    public Set<Artist> extractUniqueArtists(Map<TimeRange, Paging<Artist>> artistPagingMap) {
        Set<Artist> allUniqueArtists = new HashSet<>();
        for (Map.Entry<TimeRange, Paging<Artist>> entry : artistPagingMap.entrySet()) {
            Artist[] artists = entry.getValue().getItems();
            allUniqueArtists.addAll(Arrays.asList(artists));
        }
        return allUniqueArtists;
    }

    public ArtistDTO convertArtistDetailToArtistDTO(ArtistDetail artistDetail) {

        return ArtistDTO.builder()
                .id(artistDetail.getId())
                .followersTotal(artistDetail.getFollowersTotal())
                .name(artistDetail.getName())
                .popularity(artistDetail.getPopularity())
                .externalUrl(artistDetail.getExternalUrl())
                .imageUrl(artistDetail.getImageUrl())
                .genres( artistDetail.getGenres())
                .build();
    }

    public ArtistDTO convertArtistDetailToArtistDTOForRelatedArtists(ArtistDetail artistDetail) {
        return ArtistDTO.builder()
                .id(artistDetail.getId())
                .name(artistDetail.getName())
                .externalUrl(artistDetail.getExternalUrl())
                .imageUrl(artistDetail.getImageUrl())
                .build();
    }


    public ArtistDTO convertArtistToArtistDTO(Artist artist) {
        return convertArtistDetailToArtistDTO(commonArtistService.convertArtistToArtistDetail(artist));
    }

    public ArtistDTO convertArtistToArtistDTOForRelatedArtists(Artist artist) {
        return convertArtistDetailToArtistDTOForRelatedArtists(commonArtistService.convertArtistToArtistDetail(artist));
    }

    public List<ArtistDetail> getArtistsByIds(List<String> ids) {
        return artistDetailRepository.findByIdIn(ids);
    }
}
