package com.sinogale.library.domain.event;

/**
 * Event raised when a book is returned.
 */
public class BookReturnedEvent {
    private final Long bookId;
    private final Long userId;

    public BookReturnedEvent(Long bookId, Long userId) {
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
