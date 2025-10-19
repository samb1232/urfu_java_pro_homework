package com.samb1232.catservice.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${file.storage.path:/app/photos}")
    private String storageBasePath;

    public String savePhotoFromBase64(String photoBase64) throws IOException {
        byte[] photoBytes = Base64.getDecoder().decode(photoBase64);
        return savePhoto(photoBytes);
    }

    public String savePhoto(byte[] photoBytes) throws IOException {
        Path storageDir = Paths.get(storageBasePath);
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
            LOGGER.info("Created storage directory: {}", storageDir);
        }

        String fileName = UUID.randomUUID().toString() + ".jpg";
        Path filePath = storageDir.resolve(fileName);

        Files.write(filePath, photoBytes);
        LOGGER.info("Saved photo to: {}", filePath);

        return fileName;
    }

    public byte[] readPhoto(String fileName) throws IOException {
        Path filePath = Paths.get(storageBasePath, fileName);
        if (!Files.exists(filePath)) {
            throw new IOException("Photo file not found: " + fileName);
        }
        return Files.readAllBytes(filePath);
    }

    public String readPhotoAsBase64(String fileName) throws IOException {
        byte[] photoBytes = readPhoto(fileName);
        return Base64.getEncoder().encodeToString(photoBytes);
    }

    public void deletePhoto(String fileName) {
        try {
            Path filePath = Paths.get(storageBasePath, fileName);
            Files.deleteIfExists(filePath);
            LOGGER.info("Deleted photo: {}", fileName);
        } catch (IOException e) {
            LOGGER.error("Failed to delete photo: {}", fileName, e);
        }
    }
}
