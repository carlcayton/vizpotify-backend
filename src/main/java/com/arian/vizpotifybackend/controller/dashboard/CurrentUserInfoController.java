package com.arian.vizpotifybackend.controller.dashboard;

import com.arian.vizpotifybackend.dto.ProfileHeaderDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/me")
@CrossOrigin(origins = "http://localhost:3000")
public class CurrentUserInfoController {


    @GetMapping("/profileHeader")
//    public ResponseEntity<ProfileHeaderDTO> getProfileHeader(
      public ResponseEntity<String> getProfileHeader(
            HttpServletResponse response,
            Authentication auth) throws IOException {

        System.out.println("hit_profileHeader");
        System.out.println("getDetails");
        System.out.println(auth.getDetails().toString());
        System.out.println("getName");
        System.out.println(auth.getName());
        System.out.println("getPrincipal");
        System.out.println(auth.getPrincipal().toString());
        return ResponseEntity.ok("Test");
    }

    }
