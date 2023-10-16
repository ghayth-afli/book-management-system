package com.tekup.soa.controller;

import com.tekup.soa.model.Book;
import com.tekup.soa.payload.request.BookRequest;
import com.tekup.soa.payload.response.BookResponse;
import com.tekup.soa.payload.response.MessageResponse;
import com.tekup.soa.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class BookController {
    @Autowired
    BookRepository bookRepository;

    @PostMapping("/books")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createbook(@RequestBody BookRequest book) {
        Book book1 = new Book(book.getName(),book.getAuthor(),book.getDescription(),book.getCategory(),book.getLanguage());
        bookRepository.save(book1);
        return ResponseEntity.ok(new MessageResponse("Book created successfully!"));
    }
    @GetMapping("/books")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getallbooks() {
        return ResponseEntity.ok(bookRepository.findAll());
    }

    @GetMapping("/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getbook( @PathVariable Long id) {
        return ResponseEntity.ok(bookRepository.findById(id));    }

    @DeleteMapping("/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletebook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Book deleted successfully!"));
    }

    @PutMapping("/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatebook(@PathVariable Long id,@RequestBody BookRequest book) {
        Book book1 = bookRepository.findById(id).get();
        book1.setName(book.getName());
        book1.setCategory(book.getCategory());
        book1.setAuthor(book.getAuthor());
        book1.setDescription(book.getDescription());
        book1.setLanguage(book.getLanguage());
        bookRepository.save(book1);
        return ResponseEntity.ok(new MessageResponse("Book updated successfully!"));
    }
}
