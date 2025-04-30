package com.library.service;

import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.entity.User;
import com.library.repository.BorrowRepository;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BorrowService {
    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FineService fineService;

    public BorrowService(@Lazy FineService fineService) {
        this.fineService = fineService;
    }

    @Transactional
    public Borrow borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No copies available");
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        Borrow borrow = new Borrow();
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(java.time.LocalDate.now());
        borrow.setStatus(Borrow.Status.BORROWED);
        return borrowRepository.save(borrow);
    }

    @Transactional
    public Borrow returnBook(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId).orElseThrow(() -> new RuntimeException("Borrow not found"));
        if (borrow.getStatus() == Borrow.Status.RETURNED) {
            throw new RuntimeException("Book already returned");
        }
        borrow.setStatus(Borrow.Status.RETURNED);
        borrow.setReturnDate(java.time.LocalDate.now());
        Book book = borrow.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        fineService.calculateFineForBorrow(borrow);
        return borrowRepository.save(borrow);
    }

    public List<Borrow> findAllBorrows() {
        return borrowRepository.findAll();
    }
}