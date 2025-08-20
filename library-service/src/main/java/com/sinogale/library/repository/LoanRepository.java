package com.sinogale.library.repository;

import com.sinogale.library.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}
