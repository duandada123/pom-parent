package com.sinogale.library.repository;

import com.sinogale.library.domain.LibraryUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryUserRepository extends JpaRepository<LibraryUser, Long> {
}
