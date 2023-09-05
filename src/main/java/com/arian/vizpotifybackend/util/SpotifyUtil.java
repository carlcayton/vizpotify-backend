package com.arian.vizpotifybackend.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpotifyUtil {

    public String convertScopeListToCSV(List<String> scopes){
        return String.join(", ", scopes);
    }

}
