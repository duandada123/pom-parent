package com.sinogale.library.domain.event;

/**
 * Event raised when a book is borrowed.
 */
public class BookBorrowedEvent {
    private final Long bookId;
    private final Long userId;

    public BookBorrowedEvent(Long bookId, Long userId) {
        this.bookId = bookId;
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public Long getUserId() {
        return userId;
    }
}
