package com.example.cafeis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "사용자 정보 수정 데이터")
public class MemberModifyDTO {

    @Schema(description = "사용자 이메일 주소 (변경 불가, 식별용)", example = "user0@example.com", required = true)
    private String email;

    @Schema(description = "변경할 비밀번호", example = "newPassword123", required = true)
    private String pw;

    @Schema(description = "변경할 사용자명", example = "새닉네임", required = true)
    private String nickname;
}

