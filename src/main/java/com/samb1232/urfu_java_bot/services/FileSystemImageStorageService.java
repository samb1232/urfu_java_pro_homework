package com.samb1232.urfu_java_bot.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.samb1232.urfu_java_bot.interfaces.ImageStorageService;

@Service
public class FileSystemImageStorageService implements ImageStorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemImageStorageService.class);
    private static final String IMAGE_STORAGE_PATH = "user_images/";
    private final Map<Long, List<String>> userImages = new ConcurrentHashMap<>();

    public FileSystemImageStorageService() {
        try {
            Files.createDirectories(Paths.get(IMAGE_STORAGE_PATH));
        } catch (IOException e) {
            LOGGER.error("Error creating image storage directory", e);
        }
    }

    @Override
    public void saveImage(Long userId, File imageFile, String fileId) throws IOException {
        Path userDir = Paths.get(IMAGE_STORAGE_PATH + userId);
        Files.createDirectories(userDir);
        
        Path destination = userDir.resolve(fileId + ".jpg");
        Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
        
        userImages.computeIfAbsent(userId, _ -> new ArrayList<>()).add(destination.toString());
    }

    @Override
    public List<String> getUserImages(Long userId) {
        return userImages.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Path getImagePath(Long userId, int index) {
        return Paths.get(userImages.get(userId).get(index));
    }

    @Override
    public int getUserImageCount(Long userId) {
        return userImages.getOrDefault(userId, Collections.emptyList()).size();
    }
}