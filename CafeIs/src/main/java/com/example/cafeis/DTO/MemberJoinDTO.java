package com.example.cafeis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "사용자 가입 데이터")
public class MemberJoinDTO {

    @Schema(description = "사용자 이메일 주소", example = "user0@example.com", required = true)
    private String email;

    @Schema(description = "계정 비밀번호", example = "password123", required = true)
    private String pw;

    @Schema(description = "사용자명", example = "홍길동", required = true)
    private String nickname;

    @Schema(description = "사용자 권한 유형", example = "USER", allowableValues = {"USER", "ADMIN", "EMPLOYEE"}, required = true)
    private String role;

}

