package com.arian.vizpotifybackend.common.mapper;

import com.arian.vizpotifybackend.track.AudioFeature;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;

@Mapper(componentModel = "spring")
public interface AudioFeatureMapper {


    @Mapping(target = "id", source = "audioFeatures.id")
    @Mapping(target = "acousticness", source = "audioFeatures.acousticness")
    @Mapping(target = "danceability", source = "audioFeatures.danceability")
    @Mapping(target = "energy", source = "audioFeatures.energy")
    @Mapping(target = "instrumentalness", source = "audioFeatures.instrumentalness")
    @Mapping(target = "liveness", source = "audioFeatures.liveness")
    @Mapping(target = "speechiness", source = "audioFeatures.speechiness")
    @Mapping(target = "valence", source = "audioFeatures.valence")
    @Mapping(target = "tempo", source = "audioFeatures.tempo")
    AudioFeature toAudioFeature(AudioFeatures audioFeatures);
}
