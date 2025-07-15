package com.example.cafeis.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException() {
        super("해당 리소스에 접근할 권한이 없습니다.");
    }
}

