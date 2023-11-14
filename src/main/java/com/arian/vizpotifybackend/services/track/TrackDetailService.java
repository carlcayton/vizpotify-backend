package com.arian.vizpotifybackend.services.track;

import com.arian.vizpotifybackend.dto.TrackDTO;
import com.arian.vizpotifybackend.enums.TimeRange;
import com.arian.vizpotifybackend.model.Genre;
import com.arian.vizpotifybackend.model.TrackDetail;
import com.arian.vizpotifybackend.repository.TrackDetailRepository;

import com.arian.vizpotifybackend.services.GenreService;
import com.arian.vizpotifybackend.services.artist.CommonArtistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackDetailService {

    private final TrackDetailRepository trackDetailRepository;
    private final CommonTrackService commonTrackService;

    @Transactional
    public void processAndStoreNewTrackDetails(Set<Track> allTrackDetails) {
        Set<Track> tracksNotInTable = commonTrackService.extractTrackNotInTrackTable(allTrackDetails);
        Set<TrackDetail> newTracks = tracksNotInTable.stream()
                .map(commonTrackService::convertTrackToTrackDetail)
                .collect(Collectors.toSet());
        trackDetailRepository.saveAll(newTracks);
    }

    public List<TrackDetail> getTracksByIds(List<String> ids) {
        return trackDetailRepository.findByIdIn(ids);
    }

    public TrackDTO convertTrackToTrackDTO(Track track) {
        ArtistSimplified[] artistNamesTemp = track.getArtists();
        Set<String> artistNames =  Arrays.stream(artistNamesTemp)
                .map(ArtistSimplified::getName)
                .collect(Collectors.toSet());
        int durationMs = track.getDurationMs() != null ? track.getDurationMs() : 0;
        int popularityScore = track.getPopularity() != null ? track.getPopularity() : 0;

        return TrackDTO.builder()
                .id(track.getId())
                .name(track.getName())
                .artists(artistNames)
                .duration(durationMs)
                .albumName(track.getAlbum().getName())
                .albumImageUrl(track.getAlbum().getImages()[0].getUrl())
                .popularity(popularityScore)
                .build();
    }
    public Set<Track> extractUniqueTracks(Map<TimeRange, Paging<Track>> trackPagingMap) {
        Set<Track> allUniqueTracks = new HashSet<>();
        for (Map.Entry<TimeRange, Paging<Track>> entry : trackPagingMap.entrySet()) {
            Track[] tracks = entry.getValue().getItems();
            allUniqueTracks.addAll(Arrays.asList(tracks));
        }
        return allUniqueTracks;
    }

    public TrackDTO convertTrackDetailToTrackDTO(TrackDetail trackDetail) {
        Set<String> artistNames = convertCSVToArtistList(trackDetail.getArtists());

        return TrackDTO.builder()
                .id(trackDetail.getId())
                .name(trackDetail.getName())
                .artists(artistNames)
                .duration(trackDetail.getDuration())
                .albumName(trackDetail.getAlbumName())
                .albumImageUrl(trackDetail.getAlbumImageUrl())
                .popularity(trackDetail.getPopularity())
                .build();
    }

    private Set<String> convertCSVToArtistList(String csvArtists) {
        return Arrays.stream(csvArtists.split("\\s*,\\s*")).collect(Collectors.toSet());
    }
}
