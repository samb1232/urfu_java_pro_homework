package com.samb1232.urfu_java_bot.bot.handlers;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.samb1232.urfu_java_bot.bot.CommandsData;
import com.samb1232.urfu_java_bot.bot.TelegramService;
import com.samb1232.urfu_java_bot.services.ImageStorageService;

public class MessageHandler implements BotHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    private final ImageStorageService imageStorageService;
    private final StartMenuHandler startMenuHandler;
    private final TelegramService telegramService;

    public MessageHandler(ImageStorageService imageStorageService, 
            StartMenuHandler startMenuHandler,
            TelegramService telegramService
        ) {
        this.imageStorageService = imageStorageService;
        this.startMenuHandler = startMenuHandler;
        this.telegramService = telegramService;
    }

    @Override
    public void handle(Update update) throws TelegramApiException {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        
        if (message.isCommand() && message.getText().equals(CommandsData.START_COMMAND)) {
            startMenuHandler.sendStartMenu(chatId);
        } else if (message.hasPhoto()) {
            saveUserImage(chatId, message.getPhoto());
        }
    }

    private void saveUserImage(Long chatId, List<PhotoSize> photos) throws TelegramApiException {
        PhotoSize largestPhoto = photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null);

        if (largestPhoto == null) {
            telegramService.sendMessage(chatId, "Ошибка при обработке изображения");
            return;
        }

        try {
            String fileId = largestPhoto.getFileId();
            File imageFile = telegramService.downloadFile(fileId);
            imageStorageService.saveImage(chatId, imageFile, fileId);
            telegramService.sendMessage(chatId, "Изображение успешно сохранено!");
        } catch (IOException e) {
            LOGGER.error("Error saving image", e);
            telegramService.sendMessage(chatId, "Ошибка при сохранении изображения");
        }
    }
}