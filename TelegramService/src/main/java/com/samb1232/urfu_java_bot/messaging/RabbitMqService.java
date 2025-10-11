package com.samb1232.urfu_java_bot.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.samb1232.urfu_java_bot.dto.MyCatsResponse;
import com.samb1232.urfu_java_bot.tg_bot.TelegramApiService;
import com.samb1232.urfu_java_bot.tg_bot.handlers.my_cats.MyCatsHandler;

@Service
public class RabbitMqService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqService.class);

    private final RabbitTemplate rabbitTemplate;
    private final String defaultQueueName;
    private TelegramApiService telegramApiService;
    private MyCatsHandler myCatsHandler;

    public RabbitMqService(RabbitTemplate rabbitTemplate,
                           @Value("${app.rabbitmq.queue:telegram-service-queue}") String defaultQueueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.defaultQueueName = defaultQueueName;
    }

    @Autowired
    public void setTelegramApiService(TelegramApiService telegramApiService) {
        this.telegramApiService = telegramApiService;
    }

    @Autowired
    public void setMyCatsHandler(MyCatsHandler myCatsHandler) {
        this.myCatsHandler = myCatsHandler;
    }

    public void sendToQueue(String message) {
        sendToQueue(defaultQueueName, message);
    }

    public void sendToQueue(String queueName, String message) {
        try {
            rabbitTemplate.convertAndSend(queueName, message);
            LOGGER.info("Message sent to queue '{}': {}", queueName, message);
        } catch (Exception e) {
            LOGGER.error("Failed to send message to queue '{}': {}", queueName, message, e);
        }
    }

    @RabbitListener(queues = "my_cats_response")
    public void handleMyCatsResponse(String message) {
        try {
            LOGGER.info("Received my_cats_response: {}", message);
            MyCatsResponse response = parseMyCatsResponse(message);
            if (response != null && telegramApiService != null) {
                sendCatsListToUser(response);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process my_cats_response: {}", message, e);
        }
    }

    private MyCatsResponse parseMyCatsResponse(String json) {
        try {
            int chatIdStart = json.indexOf("\"chatId\":") + 9;
            int chatIdEnd = json.indexOf(",", chatIdStart);
            if (chatIdEnd == -1) chatIdEnd = json.indexOf("}", chatIdStart);
            Long chatId = Long.valueOf(json.substring(chatIdStart, chatIdEnd).trim());
            
            return new MyCatsResponse(chatId, new java.util.ArrayList<>());
        } catch (Exception e) {
            LOGGER.error("Failed to parse my_cats_response JSON", e);
            return null;
        }
    }

    private void sendCatsListToUser(MyCatsResponse response) {
        if (myCatsHandler != null) {
            myCatsHandler.setCatsForChat(response.getChatId(), response.getCats());
        }
        
        if (response.getCats().isEmpty()) {
            telegramApiService.sendMessageWithKeyboard(response.getChatId(), 
                com.samb1232.urfu_java_bot.constants.TextFields.MY_CATS_EMPTY_TEXT,
                com.samb1232.urfu_java_bot.tg_bot.factories.KeyboardFactory.createBackToMainMenuKeyboard());
        } else {
            telegramApiService.sendMessageWithKeyboard(response.getChatId(),
                com.samb1232.urfu_java_bot.constants.TextFields.MY_CATS_LIST_TEXT,
                com.samb1232.urfu_java_bot.tg_bot.factories.KeyboardFactory.createCatsListKeyboard(response.getCats()));
        }
    }
}
