package com.solution.library.dto;

import jakarta.validation.constraints.NotNull;

public record BorrowRequestDto(
        @NotNull
        Long bookId
) {
}
