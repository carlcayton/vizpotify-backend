
package com.arian.vizpotifybackend.artist;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArtistDto {

    private String id;
    private Integer followersTotal;
    private String name;
    private Integer popularity;
    private String externalUrl;
    private String imageUrl;
    private List<String> genres;
    private String rank;
}
