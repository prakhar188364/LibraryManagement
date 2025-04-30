package com.library.controller;

import com.library.dto.LoginRequest;
import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.entity.Fine;
import com.library.entity.User;
import com.library.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/library")
public class LibraryController {
    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private FineService fineService;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //health
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service is UP");
    }


    // Authentication
    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        System.out.println("Login request: " + request);
        try{
            User user = userService.findByUsername(request.getUsername());
            System.out.println(user.getPassword());
            if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtService.generateToken(user.getUsername());
                return ResponseEntity.ok(token);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.status(401).build();
    }

    // Book CRUD Operations
    @PostMapping("/books")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    public Book createBook(@RequestBody Book book, Authentication authentication) {
        System.out.println("Creating book: " + authentication.getAuthorities());
        return bookService.saveBook(book);
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookService.findAllBooks();
    }

    @GetMapping("/books/{id}")
    public Book getBook(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PutMapping("/books/{id}")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book) {
        return bookService.updateBook(id, book);
    }

    @DeleteMapping("/books/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok().build();
    }

    // User CRUD Operations
    @PostMapping("/users")
    //@PreAuthorize("hasRole('ADMIN')")
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping("/users")
    //@PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/users/{id}")
    //@PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/users/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/users/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // Borrow Operations
    @PostMapping("/borrow")
    //@PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN')")
    public Borrow borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        return borrowService.borrowBook(userId, bookId);
    }

    @PutMapping("/borrow/return/{borrowId}")
    //@PreAuthorize("hasRole('MEMBER') or hasRole('LIBRARIAN')")
    public Borrow returnBook(@PathVariable Long borrowId) {
        return borrowService.returnBook(borrowId);
    }

    @GetMapping("/borrow")
    //@PreAuthorize("hasRole('LIBRARIAN')")
    public List<Borrow> getAllBorrows() {
        return borrowService.findAllBorrows();
    }


    @GetMapping("/fines")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    public List<Fine> getAllFines() {
        return fineService.getAllFines();
    }

    @PostMapping("/fines")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    public Fine createFine(@RequestBody Fine fine) {
        return fineService.saveFine(fine);
    }

    @GetMapping("/fines/user/{userId}")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    public List<Fine> getFinesByUser(@PathVariable Long userId) {
        return fineService.getFinesByUser(userId);
    }

    @PutMapping("/fines/{fineId}/pay")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_LIBRARIAN')")
    public Fine markFineAsPaid(@PathVariable Long fineId) {
        return fineService.markFineAsPaid(fineId);
    }
}