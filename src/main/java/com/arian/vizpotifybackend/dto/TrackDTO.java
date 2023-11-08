package com.arian.vizpotifybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackDTO {

    private String id;

    private String name;

    private Set<String> artists;

    private String duration; // mm:ss

    private String albumName;

    private String albumImageUrl;

    private int popularity;
}
