package ru.practicum.ewm.utils;

import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatterUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Named("parseStringToDate")
    public static LocalDateTime parseStringToDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateString, FORMATTER);
    }

    @Named("formatDateToString")
    public static String formatDateToString(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.format(FORMATTER);
    }
}
