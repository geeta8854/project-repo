package com.example.library;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookService {
    private final List<Book> books = new CopyOnWriteArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public List<Book> findAll() {
        return List.copyOf(books);
    }

    public Book addBook(String title, String author) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Book title is required.");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author is required.");
        }
        Book book = new Book(nextId.getAndIncrement(), title.trim(), author.trim());
        books.add(book);
        return book;
    }
}
