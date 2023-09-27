package com.arian.vizpotifybackend.services.artist;

import com.arian.vizpotifybackend.model.ArtistDetail;
import com.arian.vizpotifybackend.repository.ArtistDetailRepository;
import com.arian.vizpotifybackend.services.GenreService;
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
    private final GenreService genreService;


    public Set<Artist> extractArtistNotInArtistTable(Set<Artist> artists) {

        Set<String> artistsId = new HashSet<String>();
        for(Artist artist: artists){
            artistsId.add(artist.getId());
        }
        List<String> existingArtists = artistDetailRepository.findExistingIds(artistsId);

        existingArtists.forEach(artistsId::remove);

        return artists.stream()
                .filter(artist -> artistsId.contains(artist.getId()))
                .collect(Collectors.toSet());
    }
    public ArtistDetail convertArtistToArtistDetail(Artist artist) {
        return ArtistDetail.builder()
                .id(artist.getId())
                .followersTotal(artist.getFollowers().getTotal())
                .externalUrl(artist.getExternalUrls().get("spotify"))
                .name(artist.getName())
                .popularity(artist.getPopularity())
                .imageUrl(artist.getImages()[0].getUrl())
                .genres(genreService.convertStringArrGenreToGenreObj(artist.getGenres()))
                .build();
    }

    public Set<String> extractIdsFromArtistDetails(List<ArtistDetail> artistDetailList) {
        return artistDetailList.stream()
                .map(ArtistDetail::getId)
                .collect(Collectors.toSet());
    }


}
