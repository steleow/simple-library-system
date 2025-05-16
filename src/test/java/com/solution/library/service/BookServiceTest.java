package com.solution.library.service;

import com.solution.library.dto.BookDto;
import com.solution.library.dto.BorrowRequestDto;
import com.solution.library.dto.ReturnRequestDto;
import com.solution.library.entity.Book;
import com.solution.library.entity.Borrower;
import com.solution.library.repository.BookRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class BookServiceTest {
    private BookRepository bookRepository;
    private BorrowerService borrowerService;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        borrowerService = mock(BorrowerService.class);
        bookService = new BookService(bookRepository, borrowerService);
    }

    @Test
    void register_shouldSaveBook_whenIsbnNotExists() throws BadRequestException {
        BookDto bookDto = new BookDto("123", "Title", "Author", null, null, null);
        when(bookRepository.findByIsbn("123")).thenReturn(Collections.emptyList());

        BookDto result = bookService.register(bookDto);

        assertEquals(bookDto, result);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void register_shouldThrow_whenIsbnExistsWithDifferentTitleOrAuthor() {
        BookDto bookDto = new BookDto("123", "Title", "Author", null, null, null);
        Book existing = new Book();
        existing.setIsbn("123");
        existing.setTitle("OtherTitle");
        existing.setAuthor("Author");
        when(bookRepository.findByIsbn("123")).thenReturn(List.of(existing));

        assertThrows(BadRequestException.class, () -> bookService.register(bookDto));
    }

    @Test
    void findAll_shouldReturnBookDtos() {
        Book book = new Book();
        book.setIsbn("123");
        book.setTitle("Title");
        book.setAuthor("Author");
        Borrower borrower = new Borrower();
        borrower.setId(1L);
        book.setBorrower(borrower);
        book.setBorrowDate(LocalDateTime.now());
        book.setReturnDate(LocalDateTime.now());
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<BookDto> result = bookService.findAll();

        assertEquals(1, result.size());
        assertEquals("123", result.get(0).isbn());
    }

    @Test
    void borrowBook_shouldSetBorrowerAndDates() throws BadRequestException {
        BorrowRequestDto request = new BorrowRequestDto(1L);
        Borrower borrower = new Borrower();
        borrower.setId(2L);
        Book book = new Book();
        book.setId(1L);
        when(borrowerService.findById(2L)).thenReturn(borrower);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.borrowBook(2L, request);

        assertEquals(borrower, book.getBorrower());
        assertNotNull(book.getBorrowDate());
        verify(bookRepository).save(book);
    }

    @Test
    void borrowBook_shouldThrow_whenBookNotFound() {
        var request = new BorrowRequestDto(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> bookService.borrowBook(2L, request));
    }

    @Test
    void borrowBook_shouldThrow_whenBookAlreadyBorrowed() {
        var request = new BorrowRequestDto(1L);
        var book = new Book();
        book.setId(1L);
        book.setBorrower(new Borrower());
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(BadRequestException.class, () -> bookService.borrowBook(2L, request));
    }

    @Test
    void returnBook_shouldClearBorrowerAndSetReturnDate() throws BadRequestException {
        var request = new ReturnRequestDto(1L);
        var book = new Book();
        book.setId(1L);
        var borrower = new Borrower();
        borrower.setId(2L);
        book.setBorrower(borrower);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.returnBook(2L, request);

        assertNull(book.getBorrower());
        assertNotNull(book.getReturnDate());
        verify(bookRepository).save(book);
    }

    @Test
    void returnBook_shouldThrow_whenBookNotFound() {
        var request = new ReturnRequestDto(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> bookService.returnBook(2L, request));
    }

    @Test
    void returnBook_shouldThrow_whenBookNotBorrowedByThisBorrower() {
        var request = new ReturnRequestDto(1L);
        var book = new Book();
        book.setId(1L);
        var borrower = new Borrower();
        borrower.setId(3L);
        book.setBorrower(borrower);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        assertThrows(BadRequestException.class, () -> bookService.returnBook(2L, request));
    }
}
