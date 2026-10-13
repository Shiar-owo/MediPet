package com.medipet.medipet.shared.exception;

import com.medipet.medipet.shared.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/resource");
    }

    @Test
    void shouldReturnConflictWhenDataIntegrityViolation() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleDataIntegrity(new DataIntegrityViolationException("duplicate key"), request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("CONFLICT", response.getBody().getError().getCode());
        assertEquals("Request conflicts with the current state of the resource",
                response.getBody().getError().getMessage());
        assertEquals("/api/resource", response.getBody().getError().getPath());
    }
}
