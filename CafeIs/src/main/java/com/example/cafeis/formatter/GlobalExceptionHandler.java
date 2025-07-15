package com.example.cafeis.formatter;


import com.example.cafeis.DTO.ApiResponseDTO;
import com.example.cafeis.exception.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {


    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> catchMemberNotFoundException(MemberNotFoundException e) {
        log.error("회원을 찾을 수 없음: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.<Void>builder()
                        .success(false)
                        .message(e.getMessage())
                        .build());
    }


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDTO<Void>> catchBusinessException(BusinessException e) {
        log.error("비즈니스 예외 발생: {}", e.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ApiResponseDTO.<Void>builder()
                        .success(false)
                        .message(e.getMessage())
                        .build());
    }


    @ExceptionHandler({EntityNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ApiResponseDTO<Void>> catchNotFoundException(RuntimeException e) {
        log.error("리소스를 찾을 수 없음: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.<Void>builder()
                        .success(false)
                        .message(e.getMessage())
                        .build());
    }


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponseDTO<Void>> catchUnauthorizedException(UnauthorizedException e) {
        log.error("인증되지 않은 접근: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDTO.<Void>builder()
                        .success(false)
                        .message(e.getMessage())
                        .build());
    }


    @ExceptionHandler({AccessDeniedException.class, ForbiddenException.class})
    public ResponseEntity<ApiResponseDTO<Void>> catchAccessDeniedException(RuntimeException e) {
        log.error("접근 권한 없음: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponseDTO.<Void>builder()
                        .success(false)
                        .message("접근 권한이 없습니다.")
                        .build());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> catchValidationExceptions(
            MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("유효성 검증 실패: {}", errors);

        return ResponseEntity
                .badRequest()
                .body(ApiResponseDTO.<Map<String, String>>builder()
                        .success(false)
                        .message("입력값 검증에 실패했습니다.")
                        .data(errors)
                        .build());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Void>> catchIllegalArgumentException(IllegalArgumentException e) {
        log.error("잘못된 인자: {}", e.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ApiResponseDTO.<Void>builder()
                        .success(false)
                        .message("잘못된 요청입니다: " + e.getMessage())
                        .build());
    }



    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponseDTO<Void>> catchNoSuchElementException(NoSuchElementException e) {
        log.error("요소를 찾을 수 없음: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.<Void>builder()
                        .success(false)
                        .message(e.getMessage())
                        .build());
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> catchNoResourceFoundException(NoResourceFoundException e) {

        if (!e.getMessage().contains("favicon.ico")) {
            log.error("리소스를 찾을 수 없음: {}", e.getMessage());
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.<Void>builder()
                        .success(false)
                        .message("요청한 리소스를 찾을 수 없습니다.")
                        .build());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> catchAllUncaughtException(Exception e) {
        log.error("예상치 못한 오류 발생", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.<Void>builder()
                        .success(false)
                        .message("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                        .build());
    }
}
