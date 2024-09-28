package com.arian.vizpotifybackend.track;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackDto {

    private String id;

    private String name;

    private Set<String> artists;

    private int duration;

    private String albumName;

    private String albumImageUrlExternal;
    private String albumImageUrl;

    private int popularity;

    private String releaseDateExternal;
    private Date releaseDate;

    private Map<String, Double> audioFeatures;
}
