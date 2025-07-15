package com.example.cafeis.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자 인증 요청 데이터")
public class LoginRequestDTO {

    @Schema(description = "이메일 주소", example = "user0@example.com")
    @NotBlank(message = "이메일은 반드시 입력해야 합니다")
    @Email(message = "올바른 이메일 주소를 입력하세요")
    @Size(max = 100, message = "이메일은 100자를 넘을 수 없습니다")
    private String email;

    @Schema(description = "계정 비밀번호", example = "password123")
    @NotBlank(message = "비밀번호는 반드시 입력해야 합니다")
    @Size(min = 8, max = 100, message = "비밀번호는 최소 8자, 최대 100자로 설정하세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 영문자, 숫자, 특수기호를 각각 포함해야 합니다")
    private String password;

    @Schema(description = "자동 인증 유지 설정", example = "false")
    @Builder.Default
    private boolean reregistrantMe = false;

    @Schema(description = "인증 유지 기간(일 단위)", example = "7")
    @Min(value = 1, message = "인증 유지 기간은 1일 이상이어야 합니다")
    @Max(value = 30, message = "인증 유지 기간은 30일을 초과할 수 없습니다")
    @Builder.Default
    private int reregistrantDays = 7;
}

