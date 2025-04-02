package com.samb1232.urfu_java_bot.configuration;

import com.samb1232.urfu_java_bot.bot.KittyBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class KittyBotConfiguration {
    @Bean
    public TelegramBotsApi telegramBotsApi(KittyBot kittyBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(kittyBot);
        return api;
    }
}
