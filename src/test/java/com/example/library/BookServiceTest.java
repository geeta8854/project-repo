package com.example.library;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookServiceTest {
    @Test
    void addsBookWithTrimmedValues() {
        BookService service = new BookService();
        Book book = service.addBook("  Clean Code  ", "  Robert Martin  ");

        assertEquals(1L, book.getId());
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert Martin", book.getAuthor());
        assertEquals(1, service.findAll().size());
    }

    @Test
    void rejectsBlankTitle() {
        BookService service = new BookService();
        assertThrows(IllegalArgumentException.class,
                () -> service.addBook(" ", "An Author"));
    }
}
