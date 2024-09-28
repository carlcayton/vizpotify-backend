package com.arian.vizpotifybackend.track;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@JsonDeserialize(as = AudioFeature.class)
@Entity
@Table(name= "audio_feature")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
