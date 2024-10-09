package com.arian.vizpotifybackend.analytics.artist;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserArtistTrackCountMapper {
    UserArtistTrackCountDto toDto(UserArtistTrackCount userArtistTrackCount);

    @Mapping(target = "userSpotifyId", source = "userSpotifyId")
    @Mapping(target = "artistTrackCountsByTimeRange", expression = "java(mapArtistTrackCountsByTimeRange(userArtistTrackCounts))")
    UserArtistTrackCountMapDto toMapDto(String userSpotifyId, List<UserArtistTrackCount> userArtistTrackCounts);

    default Map<String, List<UserArtistTrackCountDto>> mapArtistTrackCountsByTimeRange(List<UserArtistTrackCount> userArtistTrackCounts) {
        return userArtistTrackCounts.stream()
                .collect(Collectors.groupingBy(
                        UserArtistTrackCount::getTimeRange,
                        Collectors.mapping(this::toDto, Collectors.toList())
                ));
    }
}
