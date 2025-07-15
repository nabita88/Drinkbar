package com.example.cafeis.DTO;

import com.example.cafeis.Enum.RegistrantRole;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자 데이터")
public class MemberDTO implements UserDetails {

    @Schema(description = "이메일 주소", example = "user0@example.com")
    @NotBlank(message = "이메일은 반드시 입력해야 합니다")
    @Email(message = "올바른 이메일 주소를 입력하세요")
    @Size(max = 100, message = "이메일은 100자를 넘을 수 없습니다")
    private String email;

    @Schema(description = "사용자 비밀번호", example = "password123")
    @NotBlank(message = "비밀번호는 반드시 입력해야 합니다")
    @Size(min = 8, max = 100, message = "비밀번호는 최소 8자, 최대 100자로 설정하세요")
    private String password;

    @Schema(description = "사용자명", example = "커피러버")
    @NotBlank(message = "닉네임은 반드시 입력해야 합니다")
    @Size(min = 2, max = 20, message = "닉네임은 최소 2자, 최대 20자로 설정하세요")
    private String nickname;

    @Schema(description = "사용자 권한 목록", example = "[\"USER\"]")
    private List<RegistrantRole> registrantRoleList;

    // UserDetails 인터페이스 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (registrantRoleList == null) {
            return new ArrayList<>();
        }
        return registrantRoleList.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // 기존 메서드들
    public boolean hasRole(RegistrantRole role) {
        return registrantRoleList != null && registrantRoleList.contains(role);
    }

    public boolean isAdmin() {
        return hasRole(RegistrantRole.ADMIN);
    }

    public boolean isEmployee() {
        return hasRole(RegistrantRole.EMPLOYEE);
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("nickname", nickname);

        List<String> roleNames = registrantRoleList != null ?
                registrantRoleList.stream()
                        .map(RegistrantRole::name)
                        .collect(Collectors.toList()) :
                new ArrayList<>();

        claims.put("roles", roleNames);
        return claims;
    }


}
