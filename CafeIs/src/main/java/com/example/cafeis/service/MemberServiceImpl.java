package com.example.cafeis.service;


import com.example.cafeis.Domain.Registrant;
import com.example.cafeis.Enum.RegistrantRole;
import com.example.cafeis.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository registrantRepository;




    private void validateMemberRole(String role) {
        Optional.ofNullable(role)
                .filter(r -> Stream.of("USER", "ADMIN", "EMPLOYEE").anyMatch(r::equals))
                .orElseThrow(() -> new RuntimeException("올바른 회원 유형을 선택해주세요. (USER, ADMIN 또는 EMPLOYEE)"));
    }

    private void assignMemberRole(Registrant account, String role, String userEmail) {
        Optional.ofNullable(role)
                .ifPresent(r -> {
                    Stream.of(
                                    new Object[]{"EMPLOYEE", RegistrantRole.EMPLOYEE, "직원"},
                                    new Object[]{"ADMIN", RegistrantRole.ADMIN, "매니저"},
                                    new Object[]{"USER", RegistrantRole.USER, "일반"}
                            )
                            .filter(roleInfo -> r.equals(roleInfo[0]))
                            .findFirst()
                            .ifPresentOrElse(
                                    roleInfo -> {
                                        account.addMemberRole((RegistrantRole) roleInfo[1]);
                                    },
                                    () -> {
                                        account.addMemberRole(RegistrantRole.USER);
                                    }
                            );
                });
    }


}
