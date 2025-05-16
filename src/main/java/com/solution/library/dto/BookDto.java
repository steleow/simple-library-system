package com.solution.library.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record BookDto(
        @NotBlank
        String isbn,

        @NotBlank
        String title,

        @NotBlank
        String author,

        Long borrowerId,

        LocalDateTime borrowDate,

        LocalDateTime returnDate
) {
}
