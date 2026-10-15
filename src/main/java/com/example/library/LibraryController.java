package com.example.library;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LibraryController {
    private final BookService bookService;

    public LibraryController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "index";
    }

    @PostMapping("/books")
    public String addBook(@RequestParam String title,
                          @RequestParam String author,
                          RedirectAttributes redirectAttributes) {
        try {
            bookService.addBook(title, author);
            redirectAttributes.addFlashAttribute("message", "Book added successfully!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/";
    }
}
