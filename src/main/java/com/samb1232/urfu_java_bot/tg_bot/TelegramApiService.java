package com.samb1232.urfu_java_bot.tg_bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.samb1232.urfu_java_bot.dto.TGUser;
import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.dto.UserCallback;
import com.samb1232.urfu_java_bot.dto.UserMessage;


public class TelegramApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramApiService.class);
    private final TelegramLongPollingBot bot;

    public TelegramApiService(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    public void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);

        try {
            bot.execute(sendMessage);
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
                user.getLastName()
            );
            Long chatId = telegramMessage.getChatId();
            
            userMessage = new UserMessage(
                telegramMessage.getText(),
                tgUser, 
                chatId
            );
        }
        return new UpdateInfo(userMessage, userCallbackQuery);
    } 
    
    public void sendMessageWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);
        
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("Error sending message with keyboard", e);
        }
    }

    public void answerCallbackQuery(String callbackQueryId) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);
        
        try {
            bot.execute(answer);
        } catch (TelegramApiException e) {
            LOGGER.error("Error answering callback query", e);
        }
    }
}