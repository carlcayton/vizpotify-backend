package com.arian.vizpotifybackend.services.track;

import com.arian.vizpotifybackend.model.TrackDetail;
import com.arian.vizpotifybackend.repository.TrackDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonTrackService {

    private final TrackDetailRepository trackDetailRepository;

    public Set<Track> extractTrackNotInTrackTable(Set<Track> tracks) {
        Set<String> trackIds = new HashSet<>();
        for (Track track : tracks) {
            trackIds.add(track.getId());
        }
        List<String> existingTracks = trackDetailRepository.findExistingIds(trackIds);

        existingTracks.forEach(trackIds::remove);

        return tracks.stream()
                .filter(track -> trackIds.contains(track.getId()))
                .collect(Collectors.toSet());
    }


    public TrackDetail convertTrackToTrackDetail(Track track) {
        return TrackDetail.builder()
                .id(track.getId())
                .name(track.getName())
                .artists(convertArtistsToCSV(track.getArtists()))
                .duration(track.getDurationMs())
                .albumName(track.getAlbum().getName())
                .albumImageUrl(track.getAlbum().getImages()[0].getUrl())
                .popularity(track.getPopularity())
                .releaseDate(parseReleaseDate(track.getAlbum().getReleaseDate()))
                .build();
    }

    private Date parseReleaseDate(String releaseDateString) {
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        try {
            return fullDateFormat.parse(releaseDateString);
        } catch (ParseException e) {
            try {
                return yearFormat.parse(releaseDateString);
            } catch (ParseException ex) {
                return null;
            }
        }
    }



    private String convertArtistsToCSV(ArtistSimplified[] artists) {
        return Arrays.stream(artists)
                .map(ArtistSimplified::getName)
                .collect(Collectors.joining(","));
    }
    public Set<String> extractIdsFromTrackDetails(List<TrackDetail> trackDetailList) {
        return trackDetailList.stream()
                .map(TrackDetail::getId)
                .collect(Collectors.toSet());
    }
}
