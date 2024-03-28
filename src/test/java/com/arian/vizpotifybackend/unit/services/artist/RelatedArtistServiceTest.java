package com.arian.vizpotifybackend.unit.services.artist;

import com.arian.vizpotifybackend.dto.ArtistDTO;
import com.arian.vizpotifybackend.mapper.ArtistMapper;
import com.arian.vizpotifybackend.services.artist.RelatedArtistService;
import com.arian.vizpotifybackend.services.redis.ArtistCacheService;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
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
    private List<ArtistDTO> relatedArtists;

    @BeforeEach
    void setUp() {
        artistId = "artist123";
        relatedArtists = new ArrayList<>();
        relatedArtists.add(new ArtistDTO());
        relatedArtists.add(new ArtistDTO());
    }

    @Test
    void getRelatedArtists_shouldReturnCachedRelatedArtists_whenAvailable() {
        when(artistCacheService.getRelatedArtistsFromCache(artistId)).thenReturn(Optional.of(relatedArtists));

        List<ArtistDTO> result = relatedArtistService.getRelatedArtists(artistId);

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
        when(artistMapper.artistToArtistDTO(any(Artist.class))).thenReturn(new ArtistDTO());

        List<ArtistDTO> result = relatedArtistService.fetchFromSpotifyAndStoreRelatedArtists(artistId);

        assertEquals(2, result.size());
        verify(spotifyService).getRelatedArtists(artistId);
        verify(artistMapper, times(2)).artistToArtistDTO(any(Artist.class));
        verify(artistCacheService).cacheRelatedArtists(eq(artistId), eq(result));
    }
}