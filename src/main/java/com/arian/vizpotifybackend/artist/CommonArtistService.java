
package com.arian.vizpotifybackend.artist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Artist;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonArtistService {


    private final ArtistDetailRepository artistDetailRepository;


    public Set<Artist> extractArtistNotInArtistTable(Set<Artist> artists) {

        Set<String> artistsId = new HashSet<>();
        for(Artist artist: artists){
            artistsId.add(artist.getId());
        }
        List<String> existingArtists = artistDetailRepository.findExistingIds(artistsId);

        existingArtists.forEach(artistsId::remove);

        return artists.stream()
                .filter(artist -> artistsId.contains(artist.getId()))
                .collect(Collectors.toSet());
    }


    public Set<String> extractIdsFromArtistDetails(List<ArtistDetail> artistDetailList) {
        return artistDetailList.stream()
                .map(ArtistDetail::getId)
                .collect(Collectors.toSet());
    }

}
