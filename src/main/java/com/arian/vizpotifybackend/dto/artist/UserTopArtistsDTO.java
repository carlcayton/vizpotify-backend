package com.arian.vizpotifybackend.dto.artist;

import com.arian.vizpotifybackend.enums.TimeRange;
import com.arian.vizpotifybackend.model.ArtistDetail;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class UserTopArtistsDTO {

   private Map<TimeRange,ArtistDTO> result;

}
