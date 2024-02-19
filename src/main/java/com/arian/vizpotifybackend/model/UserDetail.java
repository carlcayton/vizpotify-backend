package com.arian.vizpotifybackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "user_detail")
@NoArgsConstructor
@AllArgsConstructor
public class UserDetail {

    @Id
    @Column(unique = true, nullable = false)
    private String spotifyId;

    @Column
    private String email;

    @Column
    private String country;

    @Column
    private String displayName;

    @Column
    private String externalSpotifyUrl;

    @Column
    private String followersHref;

    @Column
    private Integer followersTotal;

    @Column
    private String profileHref;

    @Column
    private String product;

    @Column
    private String profileType;

    @Column
    private String profilePictureUrl;

    @Column
    private String profileUri;

    @Column(name = "is_display_name_public", nullable = false, columnDefinition = "boolean default true")
    private Boolean isDisplayNamePublic = true;

    @Column(name = "is_profile_public", nullable = false, columnDefinition = "boolean default true")
    private Boolean isProfilePublic = true;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private boolean analyticsAvailable;
}
