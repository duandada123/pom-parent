package com.sinogale.library.service;

import com.sinogale.library.domain.Book;
import com.sinogale.library.domain.LibraryUser;
import com.sinogale.library.domain.Loan;
import com.sinogale.library.repository.BookRepository;
import com.sinogale.library.repository.LibraryUserRepository;
import com.sinogale.library.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Application service exposing basic library operations.
 */
@Service
@RequiredArgsConstructor
public class LibraryService {

    private final BookRepository bookRepository;
    private final LibraryUserRepository userRepository;
    private final LoanRepository loanRepository;

    public Book addBook(String title, String author, String isbn, String category, String location) {
        Book book = Book.create(title, author, isbn, category, location);
        return bookRepository.save(book);
    }

    public Loan borrowBook(Long bookId, Long userId, Instant dueAt) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        LibraryUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Loan loan = Loan.create(book, user, dueAt);
        return loanRepository.save(loan);
    }

    public Loan returnBook(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        loan.markReturned();
        return loanRepository.save(loan);
    }
}
