package com.arian.vizpotifybackend.dto.artist;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArtistDTO {

    private String id;
    private Integer followersTotal;
    private String name;
    private Integer popularity;
    private String externalUrl;
    private String imageUrl;
    private List<String> genres;  // Represented as strings instead of Genre entities.

    // getters, setters, etc.
}
