package com.test.SpringBootApi.service;

import com.test.SpringBootApi.domain.Book;
import com.test.SpringBootApi.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book findBook(Long id) {
        Optional<Book> result = bookRepository.findById(id);

        if (result.isPresent()) {
            return result.get();
        }

        return null;
    }

    @Override
    public Book updateBook(Long id, Book book) {
        Optional<Book> result = bookRepository.findById(id);

        if (result.isPresent()) {
            Book savedBook = result.get();

            savedBook.setBookName(book.getBookName());
            savedBook.setPrice(book.getPrice());
            savedBook.setAuthor(book.getAuthor());

            return bookRepository.save(savedBook);
        }

        return null;
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}