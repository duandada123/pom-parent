package com.sinogale.library.domain;

import com.sinogale.jpa.support.BaseJpaAggregate;
import com.sinogale.library.domain.event.BookBorrowedEvent;
import com.sinogale.library.domain.event.BookCreatedEvent;
import com.sinogale.library.domain.event.BookRemovedEvent;
import com.sinogale.library.domain.event.BookReturnedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Book aggregate root.
 */
@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
public class Book extends BaseJpaAggregate {

    @Column(nullable = false)
    private String title;

    private String author;

    @Column(unique = true)
    private String isbn;

    private String category;

    /**
     * Shelf or storage location.
     */
    private String location;

    private boolean removed;

    public static Book create(String title, String author, String isbn, String category, String location) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setCategory(category);
        book.setLocation(location);
        book.registerEvent(new BookCreatedEvent(title));
        return book;
    }

    public void borrow(Long userId) {
        registerEvent(new BookBorrowedEvent(getId(), userId));
    }

    public void returnBack(Long userId) {
        registerEvent(new BookReturnedEvent(getId(), userId));
    }

    public void remove() {
        this.removed = true;
        registerEvent(new BookRemovedEvent(getId()));
    }
}
