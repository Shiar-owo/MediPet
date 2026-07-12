package com.medipet.medipet.shared.infrastructure.outbound.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.port.outbound.FileStoragePort;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Repository
class CloudinaryAdapter implements FileStoragePort {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    private final Cloudinary cloudinary;

    CloudinaryAdapter(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String upload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BadRequestException("File type not allowed. Accepted: JPEG, PNG, GIF, WebP");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException("File size exceeds maximum of 5MB");
        }

        try {
            Map<String, Object> params = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "image");
            Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), params);
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload file: " + e.getMessage());
        }
    }
}
