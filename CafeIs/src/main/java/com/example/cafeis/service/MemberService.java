package com.example.cafeis.service;

import com.example.cafeis.DTO.MemberDTO;
import com.example.cafeis.Domain.Registrant;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MemberService {


    default MemberDTO entityToDTO(Registrant account) {
        return MemberDTO.builder()
                .email(account.getEmail())
                .password(account.getPw())
                .nickname(account.getNickname())
                .registrantRoleList(account.getRegistrantRoleList())
                .build();
    }

}
