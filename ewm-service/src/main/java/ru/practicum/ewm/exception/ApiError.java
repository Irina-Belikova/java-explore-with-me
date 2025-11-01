package ru.practicum.ewm.exception;

public record ApiError(String httpStatus, String reason, String message, String timestamp) {
}
