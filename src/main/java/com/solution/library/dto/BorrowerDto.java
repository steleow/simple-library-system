package com.solution.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record BorrowerDto(
        @NotBlank
        String name,

        @Email
        @NotBlank
        String email
) {
}
