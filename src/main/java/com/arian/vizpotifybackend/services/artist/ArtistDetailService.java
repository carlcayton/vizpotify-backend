package com.arian.vizpotifybackend.services.artist;

import com.arian.vizpotifybackend.dto.ArtistDTO;
import com.arian.vizpotifybackend.enums.TimeRange;
import com.arian.vizpotifybackend.mapper.ArtistMapper;
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
    private final ArtistMapper artistMapper;

    @Transactional
    public void processAndStoreNewArtistDetails(Set<Artist> allArtistDetails) {
        Set<ArtistDetail> artistsNotInTable = commonArtistService.extractArtistNotInArtistTable(allArtistDetails)
                .stream()
                .map(artistMapper::artistToArtistDetail)
                .collect(Collectors.toSet());

        artistDetailRepository.saveAll(artistsNotInTable);
    }

    public ArtistDTO getArtistDTOById(String artistId) {
        return artistDetailRepository.findById(artistId)
                .map(artistMapper::artistDetailToArtistDTO)
                .orElse(null);
    }

    public Set<Artist> extractUniqueArtists(Map<TimeRange, Paging<Artist>> artistPagingMap) {
        Set<Artist> allUniqueArtists = new HashSet<>();
        for (Map.Entry<TimeRange, Paging<Artist>> entry : artistPagingMap.entrySet()) {
            Artist[] artists = entry.getValue().getItems();
            allUniqueArtists.addAll(Arrays.asList(artists));
        }
        return allUniqueArtists;
    }

    public List<ArtistDetail> getArtistsByIds(List<String> ids) {
        return artistDetailRepository.findByIdIn(ids);
    }
}
