package com.arian.vizpotifybackend.dto;


import com.arian.vizpotifybackend.model.ArtistDetail;
import com.arian.vizpotifybackend.model.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtistDTO {

    private String id;
    private Integer followersTotal;
    private String name;
    private Integer popularity;
    private String externalUrl;
    private String imageUrl;
    private Set<Genre> genres;  // Represented as strings instead of Genre entities.
}
