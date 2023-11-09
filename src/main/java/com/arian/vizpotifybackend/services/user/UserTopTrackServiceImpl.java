package com.arian.vizpotifybackend.services.user;

import com.arian.vizpotifybackend.dto.TrackDTO;
import com.arian.vizpotifybackend.enums.TimeRange;
import com.arian.vizpotifybackend.model.UserTopTrack;
import com.arian.vizpotifybackend.repository.UserTopTrackRepository;
import com.arian.vizpotifybackend.services.redis.TrackCacheService;
import com.arian.vizpotifybackend.services.spotify.SpotifyService;
import com.arian.vizpotifybackend.services.track.TrackDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Track;

//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class UserTopTrackServiceImpl implements IUserTopItemService<TrackDTO, UserTopTrack> {
//
//    private final UserTopTrackRepository userTopTrackRepository;
//    private final SpotifyService spotifyService;
//    private final TrackDetailService trackDetailService;
//    private final TrackCacheService trackCacheService;
//
//
//}
