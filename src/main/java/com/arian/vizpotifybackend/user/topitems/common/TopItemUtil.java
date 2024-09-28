package com.arian.vizpotifybackend.user.topitems.common;

public class TopItemUtil {

    /**
     * Formats the given time range string for use in Data Transfer Objects (Dto).
     *
     * @param timeRange the time range string to format
     * @return a formatted string suitable for Dto naming conventions
     */
    public static String formatTimeRangeForDto(String timeRange) {
        if (timeRange == null) {
            return "";
        }
        switch (timeRange) {
            case "short_term":
                return "shortTerm";
            case "medium_term":
                return "mediumTerm";
            case "long_term":
                return "longTerm";
            default:
                return "";
        }
    }
}