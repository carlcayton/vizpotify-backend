package com.arian.vizpotifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@Table(name="user_header_stat")
@NoArgsConstructor
@AllArgsConstructor
public class UserHeaderStat {

    @Id
    private String userSpotifyId;
    private Integer followedArtistCount;
    private Integer followerCount;
    private Integer playlistCount;
}
