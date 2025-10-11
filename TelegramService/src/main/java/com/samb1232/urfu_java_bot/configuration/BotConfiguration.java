package com.samb1232.urfu_java_bot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.samb1232.urfu_java_bot.tg_bot.MainBot;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class BotConfiguration {
    @Bean
    public TelegramBotsApi telegramBotsApi(MainBot kittyBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(kittyBot);
        return api;
    }

    @Bean
    public Queue appQueue(@Value("${app.rabbitmq.queue:telegram-service-queue}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue myCatsRequestQueue() {
        return new Queue("my_cats_request", true);
    }

    @Bean
    public Queue myCatsResponseQueue() {
        return new Queue("my_cats_response", true);
    }
}
