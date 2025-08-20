package com.sinogale.library.domain;

import com.sinogale.jpa.support.BaseJpaAggregate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Library system user.
 */
@Entity
@Table(name = "library_users")
@Getter
@Setter
@NoArgsConstructor
public class LibraryUser extends BaseJpaAggregate {

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        STUDENT, TEACHER, ADMIN
    }
}
