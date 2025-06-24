package com.samb1232.urfu_java_bot.tg_bot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.samb1232.urfu_java_bot.tg_bot.MainBot;

@Configuration
public class BotConfiguration {
    @Bean
    public TelegramBotsApi telegramBotsApi(MainBot kittyBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(kittyBot);
        return api;
    }
}
