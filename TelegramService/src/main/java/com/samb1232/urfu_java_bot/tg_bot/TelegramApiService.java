package com.samb1232.urfu_java_bot.tg_bot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.samb1232.common.dto.TGUser;
import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.dto.UserCallback;
import com.samb1232.urfu_java_bot.dto.UserMessage;

@Service
public class TelegramApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramApiService.class);
    private final MainBot mainBot;

    public TelegramApiService(@Lazy MainBot mainBot) {
        this.mainBot = mainBot;
    }

    public void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);

        try {
            mainBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            LOGGER.error("Error sending message", e);
        }
    }

    public static UpdateInfo toUpdateInfo(Update update) {
        UserCallback userCallbackQuery = null;
        UserMessage userMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Long chatId = callbackQuery.getMessage().getChatId();
            userCallbackQuery = new UserCallback(callbackQuery.getData(), callbackQuery.getId(), chatId);
        }

        if (update.hasMessage()) {
            Message telegramMessage = update.getMessage();

            User user = telegramMessage.getFrom();
            TGUser tgUser = new TGUser(
                    user.getId(),
                    user.getUserName(),
                    user.getFirstName(),
                    user.getLastName());
            Long chatId = telegramMessage.getChatId();

            String photoFileId = null;
            if (telegramMessage.hasPhoto() && telegramMessage.getPhoto() != null
                    && !telegramMessage.getPhoto().isEmpty()) {
                var sizes = telegramMessage.getPhoto();
                photoFileId = sizes.get(sizes.size() - 1).getFileId();
            }

            userMessage = new UserMessage(
                    telegramMessage.getText(),
                    tgUser,
                    chatId,
                    photoFileId);
        }
        return new UpdateInfo(userMessage, userCallbackQuery);
    }

    public void sendMessageWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);

        try {
            mainBot.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("Error sending message with keyboard", e);
        }
    }

    public void answerCallbackQuery(String callbackQueryId) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);

        try {
            mainBot.execute(answer);
        } catch (TelegramApiException e) {
            LOGGER.error("Error answering callback query", e);
        }
    }

    public byte[] downloadFile(String fileId) {
        try {
            String fileUrl = getFileUrlByFileId(fileId);
            return downloadFileFromUrl(fileUrl);
        } catch (Exception e) {
            LOGGER.error("Error downloading file", e);
            return null;
        }
    }

    private String getFileUrlByFileId(String fileId) throws TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        org.telegram.telegrambots.meta.api.objects.File fileObj = mainBot.execute(getFile);
        String filePath = fileObj.getFilePath();
        return "https://api.telegram.org/file/bot" + mainBot.getBotToken() + "/" + filePath;
    }

    private byte[] downloadFileFromUrl(String fileUrl) throws Exception {
        try (InputStream inputStream = new URL(fileUrl).openStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[1024];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }

    public void sendPhoto(Long chatId, byte[] photoBytes, String caption) {
        try {
            ByteArrayInputStream inputStream = new java.io.ByteArrayInputStream(photoBytes);

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId.toString());
            sendPhoto.setPhoto(new InputFile(inputStream, "cat.jpg"));
            if (caption != null) {
                sendPhoto.setCaption(caption);
            }

            mainBot.execute(sendPhoto);
        } catch (Exception e) {
            LOGGER.error("Error sending photo", e);
        }
    }
}