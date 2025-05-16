package com.solution.library.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String isbn;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private Borrower borrower;

    private LocalDateTime borrowDate;

    private LocalDateTime returnDate;
}