package com.solution.library.controller;

import com.solution.library.dto.BorrowRequestDto;
import com.solution.library.dto.BorrowerDto;
import com.solution.library.dto.ReturnRequestDto;
import com.solution.library.service.BookService;
import com.solution.library.service.BorrowerService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrowers")
public class BorrowerController {
    private final BorrowerService borrowerService;
    private final BookService bookService;

    public BorrowerController(BorrowerService borrowerService, BookService bookService) {
        this.borrowerService = borrowerService;
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BorrowerDto> register(@Valid @RequestBody BorrowerDto borrowerDto) throws BadRequestException {
        return ResponseEntity.ok(borrowerService.register(borrowerDto));
    }

    @PostMapping("/{id}/borrow")
    public ResponseEntity<Void> borrow(@PathVariable Long id, @Valid @RequestBody BorrowRequestDto borrowRequestDto) throws BadRequestException {
        bookService.borrowBook(id, borrowRequestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<Void> returnBook(@PathVariable Long id, @Valid @RequestBody ReturnRequestDto returnRequestDto) throws BadRequestException {
        bookService.returnBook(id, returnRequestDto);
        return ResponseEntity.ok().build();
    }
}