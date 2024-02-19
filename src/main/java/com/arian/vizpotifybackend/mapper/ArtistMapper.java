package com.arian.vizpotifybackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import com.arian.vizpotifybackend.dto.ArtistDTO;
import com.arian.vizpotifybackend.model.ArtistDetail;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.ExternalUrl;
import se.michaelthelin.spotify.model_objects.specification.Image;

@Mapper(componentModel = "spring")
public abstract class ArtistMapper {

    public abstract ArtistDTO artistDetailToArtistDTO(ArtistDetail artistDetail);

    @Mapping(target = "genres", ignore = true)
    public abstract ArtistDTO artistDetailToArtistDTOForRelatedArtists(ArtistDetail artistDetail);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "followersTotal", source = "followers.total"),
            @Mapping(target = "externalUrl", expression = "java(getSpotifyUrl(artist.getExternalUrls()))"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "popularity", source = "popularity"),
            @Mapping(target = "imageUrl", expression = "java(getFirstImageUrl(artist.getImages()))"),
            @Mapping(target = "genres", source = "genres")
    })
    public abstract ArtistDetail artistToArtistDetail(Artist artist);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "followersTotal", source = "followers.total"),
            @Mapping(target = "externalUrl", expression = "java(getSpotifyUrl(artist.getExternalUrls()))"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "popularity", source = "popularity"),
            @Mapping(target = "imageUrl", expression = "java(getFirstImageUrl(artist.getImages()))"),
            @Mapping(target = "genres", source = "genres"),
            @Mapping(target = "rank", ignore = true) // Assuming rank is not available in the Artist source object
    })
    public abstract ArtistDTO artistToArtistDTO(Artist artist);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "externalUrl", expression = "java(getSpotifyUrl(artist.getExternalUrls()))"),
            @Mapping(target = "imageUrl", expression = "java(getFirstImageUrl(artist.getImages()))"),
            @Mapping(target = "genres", ignore = true),
            @Mapping(target = "followersTotal", ignore = true),
            @Mapping(target = "popularity", ignore = true),
            @Mapping(target = "rank", ignore = true)
    })
    public abstract ArtistDTO artistToArtistDTOForRelatedArtists(Artist artist);

    protected abstract String getSpotifyUrl(ExternalUrl externalUrls);

    protected abstract String getFirstImageUrl(Image[] images);
}
