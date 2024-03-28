package com.arian.vizpotifybackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackDTO {

    private String id;

    private String name;

    private Set<String> artists;

    private int duration;

    private String albumName;

    @JsonProperty("album_image_url")
    private String albumImageUrlExternal;
    private String albumImageUrl;

    private int popularity;

    @JsonProperty("release_date")
    private String releaseDateExternal;
    private Date releaseDate;

    private Map<String, Double> audioFeatures;
}
