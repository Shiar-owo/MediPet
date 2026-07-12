package com.medipet.medipet.shared.port.outbound;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    String upload(MultipartFile file, String folder);
}
