package com.samb1232.urfu_java_bot.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ImageStorageService {
    void saveImage(Long userId, File imageFile, String fileId) throws IOException;
    List<String> getUserImages(Long userId);
    Path getImagePath(Long userId, int index);
    int getUserImageCount(Long userId);
}