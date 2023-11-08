package com.arian.vizpotifybackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "track_detail")
public class TrackDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String name;

    @ElementCollection(fetch = FetchType.LAZY) // To store artists as a collection of Embeddable or Basic class
    @CollectionTable(name = "track_artists", joinColumns = @JoinColumn(name = "track_id"))
    @Column(name = "artist_name") // The name column in the track_artists table
    private List<String> artists;

    private int duration; // Duration in milliseconds

    private String albumName;

    private String albumImageUrl;

    private int popularity; // Assuming a scale of 0-100
}