package com.samb1232.urfu_java_bot.bot;

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
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.samb1232.urfu_java_bot.bot.handlers.MessageHandler;
import com.samb1232.urfu_java_bot.bot.handlers.StartMenuHandler;
import com.samb1232.urfu_java_bot.bot.keyboards.InlineKeyboardFactory;
import com.samb1232.urfu_java_bot.services.ImageStorageService;

@Component
public class KittyBot extends TelegramLongPollingBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(KittyBot.class);

    private final MessageHandler messageHandler;
    
    private final Map<Long, Integer> userImageIndex = new ConcurrentHashMap<>();
    private final ImageStorageService imageStorageService;

    public KittyBot(
            @Value("${bot.token}") String botToken,
            ImageStorageService imageStorageService) {
        super(botToken);
        this.imageStorageService = imageStorageService;
        TelegramService telegramService = new TelegramService(this);
        StartMenuHandler startMenuHandler = new StartMenuHandler(telegramService);

        this.messageHandler = new MessageHandler(imageStorageService, startMenuHandler, telegramService);
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                handleCallbackQuery(update.getCallbackQuery());
            } else if (update.hasMessage()) {
                messageHandler.handle(update);
            }
        } catch (TelegramApiException e) {
            LOGGER.error("Error handling update", e);
        }
        
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        switch (data) {
            case CallbackData.VIEW_IMAGES_CALLBACK -> showUserImages(chatId, messageId);
            case CallbackData.NEXT_IMAGE_CALLBACK -> showNextImage(chatId, messageId);
            case CallbackData.BACK_TO_START_CALLBACK -> returnToStartMenu(chatId, messageId);
        }

        answerCallbackQuery(callbackQuery.getId());
    }

    private void sendStartMenu(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Добро пожаловать! Отправьте мне изображение или просмотрите сохраненные.");

        message.setReplyMarkup(InlineKeyboardFactory.createStartMenuKeyboard());

        executeMessage(message);
    }

    private void showUserImages(Long chatId, Integer messageId) {
        List<String> images = imageStorageService.getUserImages(chatId);
        if (images.isEmpty()) {
            sendMessage(chatId, "У вас пока нет сохраненных изображений");
            return;
        }

        userImageIndex.put(chatId, 0);
        sendImageWithNavigation(chatId, messageId, 0, images.size());
    }

    private void showNextImage(Long chatId, Integer messageId) {
        int currentIndex = userImageIndex.getOrDefault(chatId, 0) + 1;
        List<String> images = imageStorageService.getUserImages(chatId);
        
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
            photo.setPhoto(new InputFile(imageStorageService.getImagePath(chatId, index).toFile()));
            
            photo.setReplyMarkup(
                InlineKeyboardFactory.createImageNavigationKeyboard(index, total)
            );
            
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