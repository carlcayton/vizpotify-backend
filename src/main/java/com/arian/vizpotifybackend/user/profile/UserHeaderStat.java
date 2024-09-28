package com.arian.vizpotifybackend.user.profile;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
