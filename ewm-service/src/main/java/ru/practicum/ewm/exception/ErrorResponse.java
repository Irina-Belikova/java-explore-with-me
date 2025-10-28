package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus status, String error, String stackTrace) {
}
