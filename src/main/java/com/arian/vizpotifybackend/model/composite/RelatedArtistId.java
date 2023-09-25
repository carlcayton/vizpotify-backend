package com.arian.vizpotifybackend.model.composite;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RelatedArtistId implements Serializable {

    private String primaryArtistId;

    private String relatedArtistId;

}
