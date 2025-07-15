package com.example.cafeis.Domain;

import com.example.cafeis.Enum.RegistrantRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_registrant", indexes = {
        @Index(name = "idx_registrant_nickname", columnList = "nickname"),
        @Index(name = "idx_registrant_email_pw", columnList = "email, pw")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "registrantRoleList")
public class Registrant {

    @Id
    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @Column(name = "pw", length = 100, nullable = false)
    private String pw;

    @Column(name = "nickname", length = 100, nullable = false)
    private String nickname;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "tbl_registrant_role",
            joinColumns = @JoinColumn(name = "registrant_email",
                    referencedColumnName = "email")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    @Builder.Default
    private List<RegistrantRole> registrantRoleList = new ArrayList<>();

    public void addMemberRole(RegistrantRole registrantRole){

        registrantRoleList.add(registrantRole);
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String pw){
        this.pw = pw;
    }

}

