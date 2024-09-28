package com.arian.vizpotifybackend.unit.services.artist;

import com.arian.vizpotifybackend.artist.ArtistDto;
import com.arian.vizpotifybackend.artist.RelatedArtistService;
import com.arian.vizpotifybackend.cache.ArtistCacheService;
import com.arian.vizpotifybackend.common.SpotifyService;
import com.arian.vizpotifybackend.common.mapper.ArtistMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.michaelthelin.spotify.model_objects.specification.Artist;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RelatedArtistServiceTest {

    @Mock
    private ArtistCacheService artistCacheService;

    @Mock
    private SpotifyService spotifyService;

    @Mock
    private ArtistMapper artistMapper;

    @InjectMocks
    private RelatedArtistService relatedArtistService;

    private String artistId;
    private List<ArtistDto> relatedArtists;

    @BeforeEach
    void setUp() {
        artistId = "artist123";
        relatedArtists = new ArrayList<>();
        relatedArtists.add(new ArtistDto());
        relatedArtists.add(new ArtistDto());
    }

    @Test
    void getRelatedArtists_shouldReturnCachedRelatedArtists_whenAvailable() {
        when(artistCacheService.getRelatedArtistsFromCache(artistId)).thenReturn(Optional.of(relatedArtists));

        List<ArtistDto> result = relatedArtistService.getRelatedArtists(artistId);

        assertEquals(relatedArtists, result);
        verify(artistCacheService).getRelatedArtistsFromCache(artistId);
        verify(spotifyService, never()).getRelatedArtists(anyString());
    }


    @Test
    void fetchFromSpotifyAndStoreRelatedArtists_shouldFetchAndStoreRelatedArtists() {
        Artist[] artists = new Artist[2];
        artists[0] = new Artist.Builder().build();
        artists[1] = new Artist.Builder().build();

        when(spotifyService.getRelatedArtists(artistId)).thenReturn(artists);
        when(artistMapper.artistToArtistDto(any(Artist.class))).thenReturn(new ArtistDto());

        List<ArtistDto> result = relatedArtistService.fetchFromSpotifyAndStoreRelatedArtists(artistId);

        assertEquals(2, result.size());
        verify(spotifyService).getRelatedArtists(artistId);
        verify(artistMapper, times(2)).artistToArtistDto(any(Artist.class));
        verify(artistCacheService).cacheRelatedArtists(eq(artistId), eq(result));
    }
}