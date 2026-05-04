package com.planner.service.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public final class DateUtil {

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtil() {}

    public static LocalDateTime now() {
        return LocalDateTime.now(IST);
    }

    public static long nowMillis() {
        return System.currentTimeMillis();
    }

    public static long startOfDayMillis() {
        return LocalDate.now(IST).atStartOfDay(IST).toInstant().toEpochMilli();
    }

    public static long endOfDayMillis() {
        return LocalDate.now(IST).atTime(23, 59, 59).atZone(IST).toInstant().toEpochMilli();
    }

    public static long startOfDayMillis(long epochMillis) {
        return Instant.ofEpochMilli(epochMillis).atZone(IST).toLocalDate().atStartOfDay(IST).toInstant().toEpochMilli();
    }

    public static String formatDate(long epochMillis) {
        return Instant.ofEpochMilli(epochMillis).atZone(IST).format(DATE_FORMAT);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMAT);
    }

    public static LocalDateTime fromEpochMillis(long millis) {
        return Instant.ofEpochMilli(millis).atZone(IST).toLocalDateTime();
    }

    public static long toEpochMillis(LocalDateTime dateTime) {
        return dateTime.atZone(IST).toInstant().toEpochMilli();
    }
}
