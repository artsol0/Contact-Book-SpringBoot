package com.artsolo.phonecontacts.exceptions;

import com.artsolo.phonecontacts.responses.DataResponse;
import com.artsolo.phonecontacts.responses.MessageResponse;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<MessageResponse> handleNoDataFoundException(NoDataFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(
                false, HttpStatus.NOT_FOUND.value(), ex.getMessage())
        );
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<MessageResponse> handleMultipartException(MultipartException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(
                false, HttpStatus.BAD_REQUEST.value(), "Request is not a multipart request")
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<MessageResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new MessageResponse(
                false, HttpStatus.PAYLOAD_TOO_LARGE.value(), "Request size is too large")
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(
                false, HttpStatus.BAD_REQUEST.value(), ex.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse<List<MessageResponse>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<MessageResponse> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) ->
            errors.add(new MessageResponse(false, HttpStatus.BAD_REQUEST.value(), error.getDefaultMessage())));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DataResponse<>(false, HttpStatus.BAD_REQUEST.value(), errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MessageResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(false, HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<MessageResponse> handleMalformedJwtException(MalformedJwtException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(
                false, HttpStatus.BAD_REQUEST.value(), "Provided invalid token")
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse(
                false, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error")
        );
    }
}
