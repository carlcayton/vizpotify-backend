package com.arian.vizpotifybackend.analytics.artist;

import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStats;
import com.arian.vizpotifybackend.analytics.features.UserTrackFeatureStatsDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserTrackFeatureStatsMapper {
    UserTrackFeatureStatsDto toDto(UserTrackFeatureStats userTrackFeatureStats);
    List<UserTrackFeatureStatsDto> toDtoList(List<UserTrackFeatureStats> userTrackFeatureStatsList);

}
