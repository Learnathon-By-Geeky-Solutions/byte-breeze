package com.bytebreeze.quickdrop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FileStorageService {

    @Value("${storage.local.path}")
    private String localUploadPath;

    public String storeFile(MultipartFile file) {

        try{
            String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(localUploadPath, uniqueFileName);

            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            return filePath.toString();

        }catch(IOException e){

            throw new RuntimeException("Failed to store file locally", e);

        }

    }

}
