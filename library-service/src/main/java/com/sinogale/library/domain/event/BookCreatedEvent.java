package com.sinogale.library.domain.event;

/**
 * Event published when a book is created.
 */
public class BookCreatedEvent {
    private final String title;

    public BookCreatedEvent(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
