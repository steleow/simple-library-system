package com.solution.library.dto;

import jakarta.validation.constraints.NotNull;

public record ReturnRequestDto(
        @NotNull
        Long bookId
) {
}
