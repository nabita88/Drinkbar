package com.example.cafeis.repository;

import com.example.cafeis.Domain.Registrant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Registrant, String> {

    @EntityGraph(attributePaths = {"registrantRoleList"})
    @Query("select m from Registrant m where m.email = :email")
    Registrant getWithRoles(@Param("email") String email);

}
