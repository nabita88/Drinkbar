package com.example.cafeis.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String email) {
        super("회원을 찾을 수 없습니다: " + email);
    }
}

