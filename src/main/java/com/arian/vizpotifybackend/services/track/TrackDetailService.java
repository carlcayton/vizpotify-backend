package com.arian.vizpotifybackend.services.track;

import com.arian.vizpotifybackend.dto.TrackDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackDetailService {

    public TrackDTO convertTrackToTrackDTO(Track track) {
        ArtistSimplified[] artistNamesTemp = track.getArtists();
        Set<String> artistNames =  Arrays.stream(artistNamesTemp)
                .map(ArtistSimplified::getName)
                .collect(Collectors.toSet());
        int durationMs = track.getDurationMs() != null ? track.getDurationMs() : 0;
        String durationFormatted = formatDuration(durationMs);
        int popularityScore = track.getPopularity() != null ? track.getPopularity() : 0;

        return TrackDTO.builder()
                .id(track.getId())
                .name(track.getName())
                .artists(artistNames)
                .duration(durationFormatted)
                .albumName(track.getAlbum().getName())
                .albumImageUrl(track.getAlbum().getImages()[0].getUrl())
                .popularity(popularityScore)
                .build();
    }
    private static String formatDuration(int durationMs) {
        int totalSeconds = durationMs / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
