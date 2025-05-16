package com.solution.library.controller;

import com.solution.library.dto.BookDto;
import com.solution.library.service.BookService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookDto> register(@Valid @RequestBody BookDto bookDto) throws BadRequestException {
        return ResponseEntity.ok(bookService.register(bookDto));
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAll() {
        return ResponseEntity.ok(bookService.findAll());
    }
}
