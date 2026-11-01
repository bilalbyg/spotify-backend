package com.spotifyclone.catalog.exception;

import com.spotifyclone.catalog.jam.application.exception.InviteLinkNotFoundException;
import com.spotifyclone.catalog.jam.application.exception.SessionNotFoundException;
import com.spotifyclone.catalog.jam.domain.exception.InviteLinkInvalidException;
import com.spotifyclone.catalog.jam.domain.exception.NotHostException;
import com.spotifyclone.catalog.jam.domain.exception.NotMemberException;
import com.spotifyclone.catalog.jam.domain.exception.SessionNotActiveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // Captures validation errors(400 bad request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, Locale locale) {
        Map<String, Object> body = new HashMap<>();
        body.put("timeStamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String resolvedMessage = messageSource.getMessage(
                    error.getDefaultMessage().replace("{", "").replace("}", ""),
                    error.getArguments(),
                    error.getDefaultMessage(),
                    locale
            );
            errors.put(error.getField(), resolvedMessage);
        });
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, Locale locale) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");

        // ex.getMessage() artık "song.not.found" anahtarını getirecek.
        // ex.getArgs() ise içindeki ID gibi dinamik verileri tutacak.
        String translatedMessage = messageSource.getMessage(
                ex.getMessage(),
                ex.getArgs(),
                ex.getMessage(),
                locale
        );

        body.put("message", translatedMessage);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // --- Jam bounded context: clients must be able to distinguish "unknown
    // --- resource" (404) from "not allowed" (403) from "state conflict" (409).

    @ExceptionHandler({SessionNotFoundException.class, InviteLinkNotFoundException.class})
    public ResponseEntity<Object> handleJamNotFound(RuntimeException ex) {
        return jamError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({NotHostException.class, NotMemberException.class})
    public ResponseEntity<Object> handleJamForbidden(RuntimeException ex) {
        return jamError(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler({
            SessionNotActiveException.class,
            InviteLinkInvalidException.class,
            OptimisticLockingFailureException.class})
    public ResponseEntity<Object> handleJamConflict(RuntimeException ex) {
        return jamError(HttpStatus.CONFLICT, ex.getMessage());
    }

    private ResponseEntity<Object> jamError(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex, Locale locale) {
        log.error("Unhandled exception", ex);
        Map<String, Object> body = new HashMap<>();

        String translatedMessage = messageSource.getMessage("generic.error", null, locale);

        body.put("message", translatedMessage);
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
