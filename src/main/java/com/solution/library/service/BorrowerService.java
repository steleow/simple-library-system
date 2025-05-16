package com.solution.library.service;

import com.solution.library.dto.BorrowerDto;
import com.solution.library.entity.Borrower;
import com.solution.library.repository.BorrowerRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public class BorrowerService {
    private final BorrowerRepository borrowerRepository;

    public BorrowerService(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    public BorrowerDto register(BorrowerDto borrowerDto) throws BadRequestException {
        if (borrowerRepository.existsByEmail(borrowerDto.email())) {
            throw new BadRequestException("Email already registered");
        }
        var borrower = new Borrower();
        borrower.setName(borrowerDto.name());
        borrower.setEmail(borrowerDto.email());
        borrowerRepository.save(borrower);
        return borrowerDto;
    }

    public Borrower findById(Long id) throws BadRequestException {
        return borrowerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Borrower not found: " + id));
    }
}
