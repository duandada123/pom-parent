package com.sinogale.library.domain.event;

/**
 * Event raised when a book is removed from collection.
 */
public class BookRemovedEvent {
    private final Long bookId;

    public BookRemovedEvent(Long bookId) {
        this.bookId = bookId;
    }

    public Long getBookId() {
        return bookId;
    }
}
