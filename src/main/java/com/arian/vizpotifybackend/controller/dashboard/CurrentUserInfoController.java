package com.arian.vizpotifybackend.controller.dashboard;

import com.arian.vizpotifybackend.dto.ProfileHeaderDTO;
import com.arian.vizpotifybackend.model.UserDetail;
import com.arian.vizpotifybackend.model.UserHeaderStat;
import com.arian.vizpotifybackend.services.user.ProfileHeaderService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/me")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CurrentUserInfoController {

    private final ProfileHeaderService profileHeaderService;

    @GetMapping("/profileHeader")
      public ResponseEntity<ProfileHeaderDTO> getProfileHeader(
            HttpServletResponse response,
            Authentication auth) throws IOException {
        UserDetail userDetail = (UserDetail) auth.getPrincipal();
       ProfileHeaderDTO profileHeaderDTO = profileHeaderService
               .getProfileHeaderDTO(userDetail.getSpotifyId());
        System.out.println(profileHeaderDTO);
        return ResponseEntity.ok(profileHeaderDTO);
    }

    }
