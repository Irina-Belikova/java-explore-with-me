package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.utils.DateFormatterUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(EwmServerException exp) {
        log.error("500{}", exp.getMessage(), exp);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exp.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exp.getMessage(), stackTrace);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleSpringValidated(MethodArgumentNotValidException e) {
        String message = e.getFieldErrors().getFirst().getDefaultMessage();
        return new ApiError(HttpStatus.BAD_REQUEST.name(), "Запрос составлен некорректно.",
                e.getMessage(), DateFormatterUtil.formatDateToString(LocalDateTime.now()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleValidation(ValidationException e) {
        return new ApiError(HttpStatus.CONFLICT.name(), "Нарушение целостности данных.",
                e.getMessage(), DateFormatterUtil.formatDateToString(LocalDateTime.now()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException e) {
        return new ApiError(HttpStatus.NOT_FOUND.name(), "Данные не найдены.",
                e.getMessage(), DateFormatterUtil.formatDateToString(LocalDateTime.now()));
    }
}
