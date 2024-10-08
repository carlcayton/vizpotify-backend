package com.arian.vizpotifybackend.common.mapper;

import com.arian.vizpotifybackend.track.TrackDto;
import com.arian.vizpotifybackend.track.TrackDetail;
import com.arian.vizpotifybackend.common.util.SpotifyUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", imports = {Arrays.class, Collectors.class})
public interface TrackMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "artists", source = "artists", qualifiedByName = "convertArtistsToCSV")
    @Mapping(target = "duration", source = "durationMs")
    @Mapping(target = "albumName", source = "album.name")
    @Mapping(target = "albumImageUrl", expression = "java(track.getAlbum().getImages()[0].getUrl())")
    @Mapping(target = "popularity", source = "popularity")
    @Mapping(target = "releaseDate", source = "album.releaseDate", qualifiedByName = "parseReleaseDate")
    TrackDetail trackToTrackDetail(Track track);

    @Mapping(target = "artists", source = "artists", qualifiedByName = "artistNames")
    @Mapping(target = "duration", source = "durationMs", defaultValue = "0")
    @Mapping(target = "popularity", source = "popularity", defaultValue = "0")
    @Mapping(target = "albumName", source = "album.name")
    @Mapping(target = "albumImageUrl", source = "album.images", qualifiedByName = "firstAlbumImageUrl")
    @Mapping(target = "releaseDate", source = "album.releaseDate", qualifiedByName = "parseReleaseDate")
    TrackDto trackToTrackDto(Track track);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "artists", source = "artists", qualifiedByName = "convertCSVToArtistList")
    @Mapping(target = "duration", source = "duration")
    @Mapping(target = "albumName", source = "albumName")
    @Mapping(target = "albumImageUrl", source = "albumImageUrl")
    @Mapping(target = "popularity", source = "popularity")
    @Mapping(target = "releaseDate", source = "releaseDate")
    TrackDto trackDetailToTrackDto(TrackDetail trackDetail);

    @Named("convertArtistsToCSV")
    public static String convertArtistsToCSV(ArtistSimplified[] artists) {
        return Arrays.stream(artists).toList().stream()
                .map(ArtistSimplified::getName)
                .collect(Collectors.joining(","));
    }
    @Named("convertCSVToArtistList")
    static Set<String> convertCSVToArtistList(String artistsCSV) {
        if (artistsCSV == null || artistsCSV.trim().isEmpty()) {
            return Set.of();
        }
        return Arrays.stream(artistsCSV.split("\\s*,\\s*"))
                .collect(Collectors.toSet());
    }

    @Named("parseReleaseDate")
    public static Date parseReleaseDate(String releaseDate) {
        return SpotifyUtil.parseReleaseDate(releaseDate);
    }
    @Named("firstAlbumImageUrl")
    default String firstAlbumImageUrl(Image[] images) {
        if (images != null && images.length > 0 && images[0] != null) {
            return images[0].getUrl();
        }
        return null;
    }
    @Named("artistNames")
    default Set<String> mapArtistsToNames(ArtistSimplified[] artists) {
        return Arrays.stream(artists)
                .map(ArtistSimplified::getName)
                .collect(Collectors.toSet());
    }
}
