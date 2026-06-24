package com.test.SpringBootApi.service;

import com.test.SpringBootApi.domain.Book;

import java.util.List;

public interface BookService {

    Book createBook(Book book);

    List<Book> findAllBooks();

    Book findBook(Long id);

    Book updateBook(Long id, Book book);

    void deleteBook(Long id);
}