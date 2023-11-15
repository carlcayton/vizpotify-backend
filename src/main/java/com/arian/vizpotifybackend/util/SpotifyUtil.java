package com.arian.vizpotifybackend.util;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class SpotifyUtil {

    public String convertScopeListToCSV(List<String> scopes){
        return String.join(", ", scopes);
    }
    public static Date parseReleaseDate(String releaseDateString) {
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        try {
            return fullDateFormat.parse(releaseDateString);
        } catch (ParseException e) {
            try {
                return yearFormat.parse(releaseDateString);
            } catch (ParseException ex) {
                return null;
            }
        }
    }

}
