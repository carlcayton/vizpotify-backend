package com.arian.vizpotifybackend.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "spotify_auth_token")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
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

