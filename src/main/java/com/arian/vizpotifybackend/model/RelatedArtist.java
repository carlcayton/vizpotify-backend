package com.arian.vizpotifybackend.model;

import com.arian.vizpotifybackend.model.composite.RelatedArtistId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatedArtist {

    @EmbeddedId
    private RelatedArtistId relatedArtistId;

}