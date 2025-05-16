package com.solution.library.service;

import com.solution.library.dto.BorrowerDto;
import com.solution.library.entity.Borrower;
import com.solution.library.repository.BorrowerRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class BorrowerServiceTest {
    private BorrowerRepository borrowerRepository;
    private BorrowerService borrowerService;

    @BeforeEach
    void setUp() {
        borrowerRepository = mock(BorrowerRepository.class);
        borrowerService = new BorrowerService(borrowerRepository);
    }

    @Test
    void register_shouldSaveBorrower_whenEmailNotExists() throws BadRequestException {
        var borrowerDto = new BorrowerDto("justin bieber", "justin.bieber@example.com");
        when(borrowerRepository.existsByEmail("justin.bieber@example.com")).thenReturn(false);

        var result = borrowerService.register(borrowerDto);

        assertEquals(borrowerDto, result);
        verify(borrowerRepository).save(any(Borrower.class));
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        var borrowerDto = new BorrowerDto("justin bieber", "justin.bieber@example.com");
        when(borrowerRepository.existsByEmail("justin.bieber@example.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> borrowerService.register(borrowerDto));
    }

    @Test
    void findById_shouldReturnBorrower_whenBorrowerExists() throws BadRequestException {
        var borrower = new Borrower();
        borrower.setId(1L);
        borrower.setName("Justin Bieber");
        borrower.setEmail("justin.bieber@example.com");
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));

        var result = borrowerService.findById(1L);

        assertEquals(borrower, result);
    }

    @Test
    void findById_shouldThrow_whenBorrowerNotFound() {
        when(borrowerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> borrowerService.findById(1L));
    }
}
