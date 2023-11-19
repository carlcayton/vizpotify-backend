package com.arian.vizpotifybackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "audio_feature")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AudioFeature {
    @Id
    private String id;
    private double acousticness;
    private double danceability;
    private double energy;
    private double instrumentalness;
    private double liveness;
    private double speechiness;
    private double valence;
    private double tempo;
}
