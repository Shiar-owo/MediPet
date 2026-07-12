package com.medipet.medipet.pet.infrastructure.inbound.rest;

import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.port.outbound.FileStoragePort;
import com.medipet.medipet.user.domain.port.outbound.TokenProvider;
import com.medipet.medipet.user.infrastructure.inbound.security.JwtAuthenticationFilter;
import com.medipet.medipet.user.infrastructure.outbound.security.SecurityUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetPhotoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PetPhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PetRepository petRepository;

    @MockitoBean
    private FileStoragePort fileStoragePort;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private SecurityUserDetailsService securityUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Pet createTestPet(String id) {
        return Pet.withId(PetId.from(UUID.fromString(id)),
                "Luna", "Dog", "Labrador", null, "Female", "Golden",
                null, null, null, null, true,
                com.medipet.medipet.client.domain.valueobject.ClientId.from(
                        UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012")));
    }

    @Test
    void shouldUploadPhotoSuccessfully() throws Exception {
        String id = UUID.randomUUID().toString();
        Pet pet = createTestPet(id);

        when(petRepository.findById(PetId.from(id))).thenReturn(Optional.of(pet));
        when(fileStoragePort.upload(any(), eq("medipet/pets")))
                .thenReturn("https://res.cloudinary.com/test/image/upload/photo.jpg");

        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "fake content".getBytes());

        mockMvc.perform(multipart("/api/pets/{id}/photo", id)
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.photoUrl").value("https://res.cloudinary.com/test/image/upload/photo.jpg"));

        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void shouldReturn404WhenPetNotFound() throws Exception {
        String id = UUID.randomUUID().toString();
        when(petRepository.findById(PetId.from(id))).thenReturn(Optional.empty());

        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "fake content".getBytes());

        mockMvc.perform(multipart("/api/pets/{id}/photo", id)
                        .file(file))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenFileTypeNotAllowed() throws Exception {
        String id = UUID.randomUUID().toString();
        Pet pet = createTestPet(id);
        when(petRepository.findById(PetId.from(id))).thenReturn(Optional.of(pet));
        when(fileStoragePort.upload(any(), eq("medipet/pets")))
                .thenThrow(new BadRequestException("File type not allowed. Accepted: JPEG, PNG, GIF, WebP"));

        MockMultipartFile file = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "content".getBytes());

        mockMvc.perform(multipart("/api/pets/{id}/photo", id)
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenFileSizeExceedsLimit() throws Exception {
        String id = UUID.randomUUID().toString();
        Pet pet = createTestPet(id);
        when(petRepository.findById(PetId.from(id))).thenReturn(Optional.of(pet));
        when(fileStoragePort.upload(any(), eq("medipet/pets")))
                .thenThrow(new BadRequestException("File size exceeds maximum of 5MB"));

        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", largeContent);

        mockMvc.perform(multipart("/api/pets/{id}/photo", id)
                        .file(file))
                .andExpect(status().isBadRequest());
    }
}
