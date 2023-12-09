package com.arian.vizpotifybackend.dto;


import com.arian.vizpotifybackend.model.ArtistDetail;
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
    private List<String> genres;
    private String rank;
}
