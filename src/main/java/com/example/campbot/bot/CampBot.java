package com.example.campbot.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.example.campbot.controller.ApplicationController;

@Component
public class CampBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final ApplicationController applicationController;

    public CampBot(@Value("${telegram.bot.token}") String botToken,
                   @Value("${telegram.bot.username}") String botUsername,
                   ApplicationController applicationController) {
        super(botToken);
        this.botUsername = botUsername;
        this.applicationController = applicationController;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            applicationController.handleUpdate(update, this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}