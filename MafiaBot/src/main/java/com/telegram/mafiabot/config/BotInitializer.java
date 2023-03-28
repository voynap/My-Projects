package com.telegram.mafiabot.config;


import com.telegram.mafiabot.service.MafiaBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Slf4j
public class BotInitializer {
    final MafiaBot mafiaBot;

    @Autowired
    public BotInitializer(MafiaBot mafiaBot) {
        this.mafiaBot = mafiaBot;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(mafiaBot);
        } catch (TelegramApiException e) {
//            log.error("Error occurred : " + e.getMessage());
        }
    }
}
