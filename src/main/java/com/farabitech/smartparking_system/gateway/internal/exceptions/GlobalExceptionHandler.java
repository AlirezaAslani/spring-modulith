package com.farabitech.smartparking_system.gateway.internal.exceptions;

import com.farabitech.smartparking_system.entry.spi.exceptions.EntryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleEntryNotFound(EntryNotFoundException ex) {
        return Map.of("error", ex.getMessage());
    }
}
