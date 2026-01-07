package com.msa.auth.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUser(DuplicateUserException e) {
        log.warn("중복 사용자 예외: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("DUPLICATE_USER", e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e) {
        log.warn("사용자 없음 예외: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidPasswordException e) {
        log.warn("비밀번호 불일치 예외: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("INVALID_PASSWORD", e.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException e) {
        log.warn("유효하지 않은 토큰 예외: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("INVALID_TOKEN", e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException e) {
        log.warn("유효하지 않은 토큰 예외: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("ENTITY_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        log.error("예상치 못한 예외 발생", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "서버 내부 오류가 발생했습니다."));
    }
}
