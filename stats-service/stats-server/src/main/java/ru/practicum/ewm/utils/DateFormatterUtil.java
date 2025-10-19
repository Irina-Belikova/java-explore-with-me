package ru.practicum.ewm.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatterUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime parseDate(String dateString) {
        return LocalDateTime.parse(dateString, FORMATTER);
    }
}
