package com.samb1232.urfu_java_bot.tg_bot.handlers.my_cats;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.statemachine.StateMachine;

import com.samb1232.urfu_java_bot.constants.MenuCallbackData;
import com.samb1232.urfu_java_bot.constants.TextFields;
import com.samb1232.urfu_java_bot.dto.CatInfo;
import com.samb1232.urfu_java_bot.dto.UpdateInfo;
import com.samb1232.urfu_java_bot.dto.UserCallback;
import com.samb1232.urfu_java_bot.dto.UserMessage;
import com.samb1232.urfu_java_bot.messaging.RabbitMqService;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.factories.KeyboardFactory;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UnknownCallbackQueryHandler;
import com.samb1232.urfu_java_bot.tg_bot.handlers.UpdateHandler;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotEvent;
import com.samb1232.urfu_java_bot.tg_bot.statemachine.BotState;


public class MyCatsHandler extends UnknownCallbackQueryHandler implements UpdateHandler  {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyCatsHandler.class);
    private final TelegramApiService telegramApiService;
    private final RabbitMqService rabbitMqService;
    
    private final Map<Long, java.util.List<CatInfo>> catsByChatId = new ConcurrentHashMap<>();

    public MyCatsHandler(TelegramApiService telegramApiService, RabbitMqService rabbitMqService) {
        super(telegramApiService);
        this.telegramApiService = telegramApiService;
        this.rabbitMqService = rabbitMqService;
    }

    @Override
    public void handle(UpdateInfo updateInfo, StateMachine<BotState, BotEvent> stateMachine) {
        if (updateInfo.hasUserCallback()) {
            processCallbackQuery(updateInfo.getUserCallback(), stateMachine);
        }
        if (updateInfo.hasUserMessage()) {
            processMessage(updateInfo.getUserMessage(), stateMachine);
        }
    }

    private void processCallbackQuery(UserCallback callbackQuery, StateMachine<BotState, BotEvent> stateMachine) {
        String callbackData = callbackQuery.getCallbackData();
        Long chatId = callbackQuery.getChatId();
        
        switch (callbackData) {
            case MenuCallbackData.BACK_TO_MAIN_MENU_CALLBACK -> {
                catsByChatId.remove(chatId);
                stateMachine.sendEvent(BotEvent.START);
                telegramApiService.sendMessageWithKeyboard(chatId, TextFields.MAIN_MENU_TEXT, KeyboardFactory.createMainMenuKeyboard());
            }
            default -> {
                if (callbackData.startsWith(MenuCallbackData.CAT_DETAILS_PREFIX)) {
                    handleCatDetailsCallback(callbackQuery);
                } else {
                    processUnkownCallbackQuery(callbackQuery, stateMachine);
                }
            }
        }
        
        telegramApiService.answerCallbackQuery(callbackQuery.getCallbackId());
    }

    private void processMessage(UserMessage userMessage, StateMachine<BotState, BotEvent> stateMachine) {
        Long userId = userMessage.getTGUser() != null ? userMessage.getTGUser().getId() : userMessage.getChatId();
        String requestMessage = String.format("{\"userId\":%d}", userId);
        
        try {
            rabbitMqService.sendToQueue("my_cats_request", requestMessage);
            LOGGER.info("Sent my_cats_request for user {}", userId);
        } catch (Exception e) {
            LOGGER.error("Failed to send my_cats_request", e);
            telegramApiService.sendMessageWithKeyboard(userMessage.getChatId(), 
                "Ошибка при получении списка котиков", 
                KeyboardFactory.createBackToMainMenuKeyboard());
        }
    }

    private void handleCatDetailsCallback(UserCallback callbackQuery) {
        String callbackData = callbackQuery.getCallbackData();
        Long chatId = callbackQuery.getChatId();
        
        try {
            String indexStr = callbackData.substring(MenuCallbackData.CAT_DETAILS_PREFIX.length());
            int catIndex = Integer.parseInt(indexStr);
            
            java.util.List<CatInfo> cats = catsByChatId.get(chatId);
            if (cats != null && catIndex >= 0 && catIndex < cats.size()) {
                CatInfo cat = cats.get(catIndex);
                String message = String.format("Имя: %s\n👍: %d\n👎: %d", 
                    cat.getName(), cat.getLikesCount(), cat.getDislikesCount());
                
                telegramApiService.sendMessage(chatId, message);
                // Send photo if available
                if (cat.getPhoto() != null && !cat.getPhoto().isEmpty()) {
                    // Note: In a real implementation, you'd need to send the photo using Telegram API
                    // For now, just send the file ID as text
                    telegramApiService.sendMessage(chatId, "Фото: " + cat.getPhoto());
                }
            } else {
                telegramApiService.sendMessage(chatId, "Котик не найден");
            }
        } catch (Exception e) {
            LOGGER.error("Error handling cat details callback", e);
            telegramApiService.sendMessage(chatId, "Ошибка при получении информации о котике");
        }
    }

    public void setCatsForChat(Long chatId, java.util.List<CatInfo> cats) {
        catsByChatId.put(chatId, cats);
    }
    
}
