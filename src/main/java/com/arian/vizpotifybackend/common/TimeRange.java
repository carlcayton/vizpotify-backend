package com.arian.vizpotifybackend.common;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum TimeRange {
    SHORT_TERM("short_term"),
    MEDIUM_TERM("medium_term"),
    LONG_TERM("long_term");

    private final String value;

    TimeRange(String value) {
        this.value = value;
    }

    public static List<String> getValuesAsList() {
        return Arrays.stream(TimeRange.values())
                .map(TimeRange::getValue)
                .collect(Collectors.toList());
    }
}

