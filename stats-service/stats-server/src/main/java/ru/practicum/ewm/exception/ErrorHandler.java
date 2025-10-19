package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(StatsServerException exp) {
        log.error("500{}", exp.getMessage(), exp);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exp.printStackTrace(pw);
        String stackTrace = sw.toString();
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exp.getMessage(), stackTrace);
    }
}
