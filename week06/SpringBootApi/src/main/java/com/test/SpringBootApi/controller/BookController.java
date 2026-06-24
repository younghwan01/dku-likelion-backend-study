package com.test.SpringBootApi.controller;

import com.test.SpringBootApi.domain.Book;
import com.test.SpringBootApi.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        Book savedBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @GetMapping
    public ResponseEntity<?> getBooks() {
        return ResponseEntity.ok(bookService.findAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBook(@PathVariable Long id) {
        Book book = bookService.findBook(id);

        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("책을 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book book) {
        Book updatedBook = bookService.updateBook(id, book);

        if (updatedBook == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("수정할 책을 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        Book book = bookService.findBook(id);

        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제할 책을 찾을 수 없습니다.");
        }

        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}