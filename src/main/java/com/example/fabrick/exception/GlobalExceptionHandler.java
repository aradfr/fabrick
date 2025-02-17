package com.example.fabrick.exception;

import com.example.fabrick.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BankingServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleBankingException(BankingServiceException ex) {
        log.error("Banking service error", ex);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus("KO");
        ApiResponse.Error error = new ApiResponse.Error();
        error.setCode("REQ001");
        error.setDescription(ex.getMessage());
        response.setErrors(java.util.Collections.singletonList(error));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus("KO");
        ApiResponse.Error error = new ApiResponse.Error();
        error.setCode("VAL001");
        error.setDescription(message);
        response.setErrors(java.util.Collections.singletonList(error));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("Unexpected error", ex);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus("KO");
        ApiResponse.Error error = new ApiResponse.Error();
        error.setCode("SYS001");
        error.setDescription("An unexpected error occurred");
        response.setErrors(java.util.Collections.singletonList(error));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
