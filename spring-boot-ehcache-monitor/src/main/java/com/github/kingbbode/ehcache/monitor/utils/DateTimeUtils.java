package com.github.kingbbode.ehcache.monitor.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public enum DateTimeUtils {
    ;

    public static String ofPattern(Long time, DateTimeFormatter formatter) {
        return toLocalDateTime(time).format(formatter);
    }

    public static LocalDateTime toLocalDateTime(Long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }
}
