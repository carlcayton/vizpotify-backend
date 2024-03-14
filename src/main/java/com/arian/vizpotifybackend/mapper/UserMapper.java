package com.arian.vizpotifybackend.mapper;
import com.arian.vizpotifybackend.model.UserDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.enums.ProductType;
import se.michaelthelin.spotify.model_objects.specification.ExternalUrl;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.User;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "id", target = "spotifyId")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "country", target = "country")
    @Mapping(source = "displayName", target = "displayName")
    @Mapping(source = "externalUrls", target = "externalSpotifyUrl", qualifiedByName = "spotifyUrl") // Fixed to use qualifiedByName
    @Mapping(source = "followers.href", target = "followersHref")
    @Mapping(source = "followers.total", target = "followersTotal")
    @Mapping(source = "href", target = "profileHref")
    @Mapping(source = "product", target = "product", qualifiedByName = "productType")
    @Mapping(source = "type", target = "profileType", qualifiedByName = "profileType")
    @Mapping(source = "images", target = "profilePictureUrl", qualifiedByName = "profilePictureUrl") // Assuming this method is correctly named and intended for use here
    @Mapping(source = "uri", target = "profileUri")
    @Mapping(target = "isDisplayNamePublic", constant = "true")
    @Mapping(target = "isProfilePublic", constant = "true")
    UserDetail userDetailToUser(User spotifyUser);

    @Named("spotifyUrl")
    default String spotifyUrl(ExternalUrl externalUrl) {
        return externalUrl != null ? externalUrl.get("spotify") : "";
    }

    @Named("productType")
    default String mapProductType(ProductType product) {
        return product == null ? "" : product.getType();
    }

    @Named("profileType")
    default String mapProfileType(ModelObjectType type) {
        return type == null ? "" : type.getType();
    }

    @Named("profilePictureUrl")
    default String mapProfilePictureUrl(Image[] images) {
        return images != null && images.length > 0 ? images[0].getUrl() : "";
    }
}
