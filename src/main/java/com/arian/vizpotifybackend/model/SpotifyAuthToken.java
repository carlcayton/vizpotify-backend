package com.arian.vizpotifybackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "spotify_auth_token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyAuthToken {

    @Id
    @Column(nullable = false)
    private String userSpotifyId;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private Integer expiresIn;

    @Column
    private LocalDateTime lastUpdated;
}

