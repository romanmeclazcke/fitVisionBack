package org.example.fitvisionback.exceptions;

import jakarta.persistence.EntityExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice  {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<String> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return ResponseEntity.status(401).body(InvalidCredentialsException.class.getSimpleName() + " : " + ex.getMessage());
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> handleEntityExistsException(EntityExistsException ex) {
        return ResponseEntity.status(409).body(EntityExistsException.class.getSimpleName() + " : " +ex.getMessage());
    }

    @ExceptionHandler(InsufficientCreditsException.class)
    public ResponseEntity<String> handleInsufficientCreditsException(InsufficientCreditsException ex) {
        return ResponseEntity.status(402).body(InsufficientCreditsException.class.getSimpleName() + " : " +ex.getMessage());
    }
}
