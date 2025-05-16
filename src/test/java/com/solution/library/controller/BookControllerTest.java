package com.solution.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solution.library.dto.BookDto;
import com.solution.library.exception.GlobalExceptionHandler;
import com.solution.library.repository.BookRepository;
import com.solution.library.service.BookService;
import com.solution.library.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private ObjectMapper objectMapper;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        bookDto = new BookDto("123", "Title", "Author", null, null, null);
    }

    @Test
    void register_shouldReturnBookDto_whenValidRequest() throws Exception {
        when(bookService.register(any(BookDto.class))).thenReturn(bookDto);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("123"))
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.author").value("Author"));
    }

    @Test
    void register_shouldReturnBadRequest_whenServiceThrowsException() throws Exception {
        when(bookService.register(any(BookDto.class)))
                .thenThrow(new BadRequestException("Invalid data"));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_shouldReturnListOfBooks() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(bookDto));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].isbn").value("123"))
                .andExpect(jsonPath("$[0].title").value("Title"))
                .andExpect(jsonPath("$[0].author").value("Author"));
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoBooksFound() throws Exception {
        when(bookService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
}
