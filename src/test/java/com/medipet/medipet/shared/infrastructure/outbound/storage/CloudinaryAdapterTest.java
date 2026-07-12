package com.medipet.medipet.shared.infrastructure.outbound.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.medipet.medipet.shared.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryAdapterTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    private CloudinaryAdapter cloudinaryAdapter;

    @BeforeEach
    void setUp() {
        cloudinaryAdapter = new CloudinaryAdapter(cloudinary);
        lenient().when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void shouldUploadFileSuccessfully() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "fake image content".getBytes());
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/test/image/upload/v1/medipet/pets/photo.jpg"));

        String url = cloudinaryAdapter.upload(file, "medipet/pets");

        assertEquals("https://res.cloudinary.com/test/image/upload/v1/medipet/pets/photo.jpg", url);
        verify(uploader).upload(any(byte[].class), eq(Map.of(
                "folder", "medipet/pets",
                "resource_type", "image")));
    }

    @Test
    void shouldThrowWhenFileTypeNotAllowed() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "content".getBytes());

        assertThrows(BadRequestException.class, () ->
                cloudinaryAdapter.upload(file, "medipet/pets"));

        verify(uploader, never()).upload(any(), any());
    }

    @Test
    void shouldThrowWhenFileSizeExceeds5MB() throws IOException {
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", largeContent);

        assertThrows(BadRequestException.class, () ->
                cloudinaryAdapter.upload(file, "medipet/pets"));

        verify(uploader, never()).upload(any(), any());
    }

    @Test
    void shouldThrowWhenFileIsEmpty() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", new byte[0]);

        assertThrows(BadRequestException.class, () ->
                cloudinaryAdapter.upload(file, "medipet/pets"));

        verify(uploader, never()).upload(any(), any());
    }

    @Test
    void shouldAcceptPngImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.png", "image/png", "fake png content".getBytes());
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/test/image/upload/photo.png"));

        String url = cloudinaryAdapter.upload(file, "medipet/pets");

        assertNotNull(url);
        verify(uploader).upload(any(byte[].class), any(Map.class));
    }

    @Test
    void shouldAcceptWebpImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.webp", "image/webp", "fake webp content".getBytes());
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/test/image/upload/photo.webp"));

        String url = cloudinaryAdapter.upload(file, "medipet/pets");

        assertNotNull(url);
    }
}
