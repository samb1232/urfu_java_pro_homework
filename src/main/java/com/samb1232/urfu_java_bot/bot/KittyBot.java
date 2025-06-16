package com.samb1232.urfu_java_bot.bot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class KittyBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(KittyBot.class);
    private static final String START_COMMAND = "/start";
    private static final String VIEW_IMAGES_CALLBACK = "view_images";
    private static final String NEXT_IMAGE_CALLBACK = "next_image";
    private static final String BACK_TO_START_CALLBACK = "back_to_start";
    
    private final Map<Long, List<String>> userImages = new ConcurrentHashMap<>();
    private final Map<Long, Integer> userImageIndex = new ConcurrentHashMap<>();
    private final String imageStoragePath = "user_images/";

    public KittyBot(@Value("${bot.token}") String botToken) {
        super(botToken);
        try {
            Files.createDirectories(Paths.get(imageStoragePath));
        } catch (IOException e) {
            LOGGER.error("Error creating image storage directory", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }
    }

    private void handleMessage(Message message) {
        Long chatId = message.getChatId();
        if (message.isCommand() && message.getText().equals(START_COMMAND)) {
            sendStartMenu(chatId);
        } else if (message.hasPhoto()) {
            saveUserImage(chatId, message.getPhoto());
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        switch (data) {
            case VIEW_IMAGES_CALLBACK -> showUserImages(chatId, messageId);
            case NEXT_IMAGE_CALLBACK -> showNextImage(chatId, messageId);
            case BACK_TO_START_CALLBACK -> returnToStartMenu(chatId, messageId);
        }

        answerCallbackQuery(callbackQuery.getId());
    }

    private void saveUserImage(Long chatId, List<PhotoSize> photos) {
        PhotoSize largestPhoto = photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null);

        if (largestPhoto == null) {
            sendMessage(chatId, "Ошибка при обработке изображения");
            return;
        }

        try {
            String fileId = largestPhoto.getFileId();
            org.telegram.telegrambots.meta.api.objects.File file = execute(org.telegram.telegrambots.meta.api.methods.GetFile.builder()
                    .fileId(fileId)
                    .build());
            
            java.io.File imageFile = downloadFile(file);
            Path userDir = Paths.get(imageStoragePath + chatId);
            Files.createDirectories(userDir);
            
            Path destination = userDir.resolve(fileId + ".jpg");
            Files.copy(imageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            
            userImages.computeIfAbsent(chatId, _ -> new ArrayList<>()).add(destination.toString());
            sendMessage(chatId, "Изображение успешно сохранено!");
        } catch (TelegramApiException | IOException e) {
            LOGGER.error("Error saving image", e);
            sendMessage(chatId, "Ошибка при сохранении изображения");
        }
    }

    private void sendStartMenu(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Добро пожаловать! Отправьте мне изображение или просмотрите сохраненные.");

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = Collections.singletonList(
                InlineKeyboardButton.builder()
                        .text("Посмотреть мои картинки")
                        .callbackData(VIEW_IMAGES_CALLBACK)
                        .build()
        );
        keyboard.setKeyboard(Collections.singletonList(row));
        message.setReplyMarkup(keyboard);

        executeMessage(message);
    }

    private void showUserImages(Long chatId, Integer messageId) {
        List<String> images = userImages.getOrDefault(chatId, Collections.emptyList());
        if (images.isEmpty()) {
            sendMessage(chatId, "У вас пока нет сохраненных изображений");
            return;
        }

        userImageIndex.put(chatId, 0);
        sendImageWithNavigation(chatId, messageId, 0, images.size());
    }

    private void showNextImage(Long chatId, Integer messageId) {
        int currentIndex = userImageIndex.getOrDefault(chatId, 0) + 1;
        List<String> images = userImages.get(chatId);
        
        if (currentIndex >= images.size()) {
            currentIndex = 0;
        }
        
        userImageIndex.put(chatId, currentIndex);
        sendImageWithNavigation(chatId, messageId, currentIndex, images.size());
    }

    private void sendImageWithNavigation(Long chatId, Integer prevMessageId, int index, int total) {
        try {
            deleteMessage(chatId, prevMessageId);
            
            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(new File(userImages.get(chatId).get(index))));
            
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            if (total > 1) {
                buttons.add(InlineKeyboardButton.builder()
                        .text("Далее (" + (index+1) + "/" + total + ")")
                        .callbackData(NEXT_IMAGE_CALLBACK)
                        .build());
            }
            
            buttons.add(InlineKeyboardButton.builder()
                    .text("Назад")
                    .callbackData(BACK_TO_START_CALLBACK)
                    .build());
            
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            keyboard.setKeyboard(Collections.singletonList(buttons));
            photo.setReplyMarkup(keyboard);
            
            execute(photo);
        } catch (TelegramApiException e) {
            LOGGER.error("Error sending photo", e);
        }
    }

    private void returnToStartMenu(Long chatId, Integer messageId) {
        try {
            deleteMessage(chatId, messageId);
            sendStartMenu(chatId);
        } catch (Exception e) {
            LOGGER.error("Error returning to start menu", e);
        }
    }

    private void answerCallbackQuery(String callbackId) {
        try {
            execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackId)
                    .build());
        } catch (TelegramApiException e) {
            LOGGER.error("Error answering callback", e);
        }
    }

    private void deleteMessage(Long chatId, Integer messageId) {
        try {
            execute(DeleteMessage.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .build());
        } catch (TelegramApiException e) {
            LOGGER.error("Error deleting message", e);
        }
    }

    private void sendMessage(Long chatId, String text) {
        executeMessage(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build());
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("Error sending message", e);
        }
    }

    @Override
    public String getBotUsername() {
        return "urfu_java_dz_bot";
    }
}