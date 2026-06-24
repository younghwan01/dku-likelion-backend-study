package com.test.SpringBootApi.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "book_name")
    private String bookName;

    @Column(name = "price")
    private int price;

    @Column(name = "author")
    private String author;

    public Book() {
    }

    public Book(String bookName, int price, String author) {
        this.bookName = bookName;
        this.price = price;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getBookName() {
        return bookName;
    }

    public int getPrice() {
        return price;
    }

    public String getAuthor() {
        return author;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}