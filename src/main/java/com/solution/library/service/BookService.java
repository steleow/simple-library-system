package com.solution.library.service;

import com.solution.library.dto.BookDto;
import com.solution.library.dto.BorrowRequestDto;
import com.solution.library.dto.ReturnRequestDto;
import com.solution.library.entity.Book;
import com.solution.library.entity.Borrower;
import com.solution.library.repository.BookRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BorrowerService borrowerService;

    public BookService(BookRepository bookRepository, BorrowerService borrowerService) {
        this.bookRepository = bookRepository;
        this.borrowerService = borrowerService;
    }

    public BookDto register(BookDto bookDto) throws BadRequestException {
        var existingBooks = bookRepository.findByIsbn(bookDto.isbn());

        if (!existingBooks.isEmpty()) {
            for (var existingBook : existingBooks) {
                if (!existingBook.getTitle().equals(bookDto.title()) ||
                        !existingBook.getAuthor().equals(bookDto.author())) {
                    throw new BadRequestException(
                            "ISBN %s already exists with a different title or author".formatted(bookDto.isbn())
                    );
                }
            }
        }
        var book = new Book();
        book.setIsbn(bookDto.isbn());
        book.setTitle(bookDto.title());
        book.setAuthor(bookDto.author());
        bookRepository.save(book);
        return bookDto;
    }

    public List<BookDto> findAll() {
        return bookRepository.findAll().stream().map(book -> new BookDto(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getBorrower() != null ? book.getBorrower().getId() : null,
                book.getBorrowDate(),
                book.getReturnDate())).collect(Collectors.toList());
    }

    public void borrowBook(Long borrowerId, BorrowRequestDto request) throws BadRequestException {
        var borrower = borrowerService.findById(borrowerId);
        var book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BadRequestException("Book not found: " + request.bookId()));
        if (book.getBorrower() != null) {
            throw new BadRequestException("Book already borrowed: " + book.getId());
        }
        book.setBorrower(borrower);
        book.setBorrowDate(LocalDateTime.now());
        book.setReturnDate(null);
        bookRepository.save(book);
    }

    public void returnBook(Long borrowerId, ReturnRequestDto request) throws BadRequestException {
        var book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BadRequestException("Book not found: " + request.bookId()));
        if (book.getBorrower() == null || !book.getBorrower().getId().equals(borrowerId)) {
            throw new BadRequestException("Book not borrowed by this borrower: " + book.getId());
        }
        book.setReturnDate(LocalDateTime.now());
        book.setBorrower(null);
        bookRepository.save(book);
    }
}
