package com.example.cafeis.repository;

import com.example.cafeis.Domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    Branch findByBranchNo(Long branchNo);
}