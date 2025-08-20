package com.sinogale.library.domain;

import com.sinogale.jpa.support.BaseJpaAggregate;
import com.sinogale.library.domain.event.BookBorrowedEvent;
import com.sinogale.library.domain.event.BookReturnedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

/**
 * Borrow/return record.
 */
@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
public class Loan extends BaseJpaAggregate {

    @ManyToOne(optional = false)
    private Book book;

    @ManyToOne(optional = false)
    private LibraryUser user;

    @Column(nullable = false)
    private Instant borrowedAt;

    @Column(nullable = false)
    private Instant dueAt;

    private Instant returnedAt;

    public static Loan create(Book book, LibraryUser user, Instant dueAt) {
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setBorrowedAt(Instant.now());
        loan.setDueAt(dueAt);
        loan.registerEvent(new BookBorrowedEvent(book.getId(), user.getId()));
        return loan;
    }

    public void markReturned() {
        this.returnedAt = Instant.now();
        registerEvent(new BookReturnedEvent(book.getId(), user.getId()));
    }
}
