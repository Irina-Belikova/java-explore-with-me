package ru.practicum.ewm.utils;

import org.mapstruct.Named;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatterUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Named("parseStringToDate")
    public static LocalDateTime parseDate(String dateString) {
        try {
            String decodedDate = URLDecoder.decode(dateString, StandardCharsets.UTF_8);
            return LocalDateTime.parse(decodedDate, FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.parse(dateString, FORMATTER);
        }
    }

    @Named("formatDateToString")
    public static String formatDateToString(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return date.format(FORMATTER);
    }
}
