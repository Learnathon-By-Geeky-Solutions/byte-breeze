package com.bytebreeze.quickdrop.service;

import org.apache.commons.io.FilenameUtils;
import com.bytebreeze.quickdrop.exception.custom.FileValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileStorageService {

    @Value("${storage.local.path}")
    private String localUploadPath;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // Allowed file extensions
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "pdf");

    // Allowed MIME types
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/jpeg", "image/png", "application/pdf");

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileValidationException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("File size exceeds the maximum limit of 5MB");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.contains("..")) {
            throw new FileValidationException("Invalid file name");
        }

        String extension = FilenameUtils.getExtension(originalFileName);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileValidationException("Unsupported file extension: " + extension);
        }

        String mimeType;
        try {
            mimeType = Files.probeContentType(Paths.get(originalFileName));
        } catch (IOException e) {
            throw new FileValidationException("Could not determine file type");
        }

        if (mimeType == null || !ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new FileValidationException("Invalid file type: " + mimeType);
        }
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }





    public String storeFile(MultipartFile file) {

        try{

            validateFile(file);


            String uniqueFileName = UUID.randomUUID() + "_" + sanitizeFileName(file.getOriginalFilename());
            Path filePath = Paths.get(localUploadPath, uniqueFileName);

            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            return filePath.toString();

        }catch(IOException e){

            throw new RuntimeException("Failed to store file locally", e);

        }

    }

}
