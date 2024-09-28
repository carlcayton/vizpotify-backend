package com.arian.vizpotifybackend.common.mapper;

import com.arian.vizpotifybackend.artist.ArtistDto;
import com.arian.vizpotifybackend.artist.ArtistDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.ExternalUrl;
import se.michaelthelin.spotify.model_objects.specification.Image;

@Mapper(componentModel = "spring")
public interface ArtistMapper {


    ArtistDto artistDetailToArtistDto(ArtistDetail artistDetail);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "followersTotal", source = "followers.total")
    @Mapping(target = "externalUrl", source = "externalUrls", qualifiedByName = "getSpotifyUrl")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "popularity", source = "popularity")
    @Mapping(target = "imageUrl", source="images", qualifiedByName = "getFirstImageUrl")
    @Mapping(target = "genres", source = "genres")
    ArtistDetail artistToArtistDetail(Artist artist);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "followersTotal", source = "followers.total")
    @Mapping(target = "externalUrl", source = "externalUrls", qualifiedByName = "getSpotifyUrl")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "popularity", source = "popularity")
    @Mapping(target = "imageUrl", source="images", qualifiedByName = "getFirstImageUrl")
    @Mapping(target = "genres", source = "genres")
    @Mapping(target = "rank", ignore = true)
    ArtistDto artistToArtistDto(Artist artist);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "externalUrl", source = "externalUrls", qualifiedByName = "getSpotifyUrl")
    @Mapping(target = "imageUrl", source="images", qualifiedByName = "getFirstImageUrl")
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "followersTotal", ignore = true)
    @Mapping(target = "popularity", ignore = true)
    @Mapping(target = "rank", ignore = true)
    ArtistDto artistToArtistDtoForRelatedArtists(Artist artist);

    @Named("getSpotifyUrl")
    default String getSpotifyUrl(ExternalUrl externalUrls) {
        return externalUrls != null ? externalUrls.get("spotify"): null;
    }

    @Named("getFirstImageUrl")
    default String getFirstImageUrl(Image[] images) {
        return (images != null && images.length > 0) ? images[0].getUrl() : null;
    }

}