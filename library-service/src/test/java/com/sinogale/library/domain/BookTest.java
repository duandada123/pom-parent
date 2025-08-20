package com.sinogale.library.domain;

import com.sinogale.library.domain.event.BookBorrowedEvent;
import org.junit.Test;

import static org.junit.Assert.*;

public class BookTest {

    @Test
    public void borrowRegistersDomainEvent() {
        Book book = Book.create("title", "author", "isbn", "cat", "loc");
        book.clearDomainEvents();
        book.borrow(1L);
        assertEquals(1, book.domainEvents().size());
        assertTrue(book.domainEvents().get(0) instanceof BookBorrowedEvent);
    }
}
