package com.example.campbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.core.io.ClassPathResource;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.example.campbot.dto.*;
import com.example.campbot.service.ApplicationService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import javax.swing.text.html.HTML;
import java.io.File;
import java.io.IOException;

@Controller
public class ApplicationController {

    private final ApplicationService applicationService;
    private final Map<Long, ApplicationState> userStates = new HashMap<>();
    private final Map<Long, ApplicationRequestDto> userApplications = new HashMap<>();

    private static final String IMAGE_1_PATH = "/images/info7.png";
    private static final String IMAGE_2_PATH = "/images/info1.jpeg";
    private static final String IMAGE_3_PATH = "/images/info5.jpg";
    private static final String IMAGE_4_PATH = "/images/info6.jpg";
    private static final Long ADMIN_TG_ID = 407457271L;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public void handleUpdate(Update update, TelegramLongPollingBot bot) throws TelegramApiException {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            if (messageText.equals("/start")) {
                sendWelcomeMessage(chatId, bot);
                showMainMenu(chatId, bot);
            } else if (messageText.equals("✏\uFE0F Оставить заявку")) {
                startApplication(chatId, bot);
            } else if (messageText.equals("\uD83D\uDCCC Узнать больше")) {
                sendInfo(chatId, bot);
            } else if (messageText.equals("\uD83D\uDCF1 Контакты")) {
                sendContacts(chatId, bot);
            } else {
                handleApplicationInput(chatId, messageText, bot);
            }
        }
    }

    private void sendWelcomeMessage(Long chatId, TelegramLongPollingBot bot) throws TelegramApiException {
        try {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId.toString());

            // Используем InputStream вместо File
            ClassPathResource resource = new ClassPathResource(IMAGE_4_PATH);
            InputFile inputFile = new InputFile(resource.getInputStream(), "welcome.jpg");
            sendPhoto.setPhoto(inputFile);

            sendPhoto.setCaption("""
                🎉 Добро пожаловать в GreenHill!
                
                За 10 лет провели больше 20 ярких смен💚
                Английский, новые знания, друзья, эмоции, заряд на весь год!
                """);

            bot.execute(sendPhoto);
        } catch (IOException e) {
            // Логируем ошибку
            System.err.println("Error loading image: " + e.getMessage());
            // Если изображение не найдено, отправляем только текст
            sendMessage(chatId, """
                🎉 Добро пожаловать в GreenHill!
                
                За 10 лет провели больше 20 ярких смен💚
                Английский, новые знания, друзья, эмоции, заряд на весь год!
                """, bot);
        }
    }


    private void showMainMenu(Long chatId, TelegramLongPollingBot bot) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Что вас интересует?");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("✏\uFE0F Оставить заявку");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("\uD83D\uDCCC Узнать больше");
        row2.add("\uD83D\uDCF1 Контакты");

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        bot.execute(message);
    }

    private void startApplication(Long chatId, TelegramLongPollingBot bot) throws TelegramApiException {
        userStates.put(chatId, ApplicationState.AWAITING_BIRTHDAY);
        userApplications.put(chatId, new ApplicationRequestDto());

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Укажите дату рождения ребенка (ДД.ММ.ГГГГ)");
        bot.execute(message);
    }

    private void handleApplicationInput(Long chatId, String messageText, TelegramLongPollingBot bot) throws TelegramApiException {
        ApplicationState state = userStates.get(chatId);
        ApplicationRequestDto application = userApplications.get(chatId);

        if (state == null || application == null) {
            return;
        }

        switch (state) {
            case AWAITING_BIRTHDAY:
                if (validateBirthday(messageText)) {
                    application.setBirthdayDate(messageText); // Просто сохраняем строку
                    userStates.put(chatId, ApplicationState.AWAITING_FULL_NAME);
                    sendMessage(chatId, "Укажите ФИО ребенка (В формате Иванов Иван Иванович)", bot);
                } else {
                    sendMessage(chatId, "❌ Неверный формат даты. Используйте ДД.ММ.ГГГГ", bot);
                }
                break;

            case AWAITING_FULL_NAME:
                if (validateFullName(messageText)) {
                    application.setChildName(messageText);
                    userStates.put(chatId, ApplicationState.AWAITING_PHONE);
                    sendMessage(chatId, "Укажите номер телефона одного из родителей (Номер должен начинаться с +7, 7 или 8 и содержать 11 цифр)", bot);
                } else {
                    sendMessage(chatId, "❌ Неверный формат ФИО. Используйте формат: Иванов Иван Иванович", bot);
                }
                break;

            case AWAITING_PHONE:
                if (validatePhone(messageText)) {
                    application.setParentPhoneNumber(messageText);
                    completeApplication(chatId, application, bot);
                    userStates.remove(chatId);
                    userApplications.remove(chatId);
                } else {
                    sendMessage(chatId, "❌ Неверный формат номера. Номер должен начинаться с +7, 7 или 8 и содержать 11 цифр", bot);
                }
                break;
        }
    }

    private void completeApplication(Long chatId, ApplicationRequestDto application, TelegramLongPollingBot bot) throws TelegramApiException {
        // Сохраняем заявку в базу
        applicationService.saveApplication(application, chatId);

        // Отправляем подтверждение пользователю
        String confirmationMessage = "✅ Ваша заявка оставлена! Мы с вами свяжемся и уточним все детали\n\n" +
                "📋 Информация по заявке:\n" +
                "👶 ФИО ребенка: " + application.getChildName() + "\n" +
                "🎂 Дата рождения: " + application.getBirthdayDate() + "\n" +
                "📞 Телефон родителя: " + application.getParentPhoneNumber();

        sendMessage(chatId, confirmationMessage, bot);

        // Отправляем копию админу
        sendMessage(ADMIN_TG_ID, "📥 Новая заявка!\n\n\n" + confirmationMessage, bot);

        // Показываем главное меню снова
        showMainMenu(chatId, bot);
    }

    private void sendInfo(Long chatId, TelegramLongPollingBot bot) throws TelegramApiException {
        try {
            List<InputMedia> mediaGroup = new ArrayList<>();

            // Первое фото с подписью
            InputMediaPhoto photo1 = new InputMediaPhoto();
            ClassPathResource resource1 = new ClassPathResource(IMAGE_1_PATH);
            photo1.setMedia(resource1.getInputStream(), "info1.jpg");
            photo1.setParseMode(ParseMode.HTML);
            photo1.setCaption("""
            Дорогие родители! Приглашаем детей на зимнюю смену <b>приключенческого лагеря Гринхилл</b> - «Остров драконов: зимнее приключение»! 🐉❄️
       
            <b>Что вас ожидает?</b>
            🔥 Увлекательные квесты и игры по мотивам легенд об огнедышащих драконах
            
            🥁 Творческие мастерские и командные испытания
            
            ❄️ Зимние забавы на свежем воздухе, зажигательные дискотеки и уютные вечера с песнями под гитару.
            5-разовое питание, кислородные коктейли и соляная шахта.
            
            🇬🇧🇰🇵🇨🇳Мастер классы на английском, китайском, французском и корейском языках!
            
            
            📍 <b>Место:</b> Павловский детский санаторий, с Павловка, Нуримановский район\s
            🗓️ <b>Дата:</b> 8-10 января 2026 г.
            
            Полных 3 дня, заезд с 8 утра 8 января, выезд 10 января после ужина\s
            
            Стоимость: <i>16 000 ₽</i>
            """);

            // Второе фото
            InputMediaPhoto photo2 = new InputMediaPhoto();
            ClassPathResource resource2 = new ClassPathResource(IMAGE_2_PATH);
            photo2.setMedia(resource2.getInputStream(), "info2.jpg");

            // Третье фото
            InputMediaPhoto photo3 = new InputMediaPhoto();
            ClassPathResource resource3 = new ClassPathResource(IMAGE_3_PATH);
            photo3.setMedia(resource3.getInputStream(), "info3.jpg");

            mediaGroup.add(photo1);
            mediaGroup.add(photo2);
            mediaGroup.add(photo3);

            SendMediaGroup mediaGroupMessage = new SendMediaGroup();
            mediaGroupMessage.setChatId(chatId.toString());
            mediaGroupMessage.setMedias(mediaGroup);

            bot.execute(mediaGroupMessage);
        } catch (IOException e) {
            System.err.println("Error loading images for info: " + e.getMessage());
            sendMessage(chatId, """
            Дорогие родители! Приглашаем детей на зимнюю смену <b>приключенческого лагеря Гринхилл</b> - «Остров драконов: зимнее приключение»! 🐉❄️
            
            <b>Что вас ожидает?</b>
            🔥 Увлекательные квесты и игры по мотивам легенд об огнедышащих драконах
            
            🥁 Творческие мастерские и командные испытания
            
            ❄️ Зимние забавы на свежем воздухе, зажигательные дискотеки и уютные вечера с песнями под гитару.
            5-разовое питание, кислородные коктейли и соляная шахта.
            
            🇬🇧🇰🇵🇨🇳Мастер классы на английском, китайском, французском и корейском языках!
            
            
            📍 <b>Место:</b> Павловский детский санаторий, с Павловка, Нуримановский район\s
            🗓️ <b>Дата:</b> 8-10 января 2026 г.
            
            Полных 3 дня, заезд с 8 утра 8 января, выезд 10 января после ужина\s
            
            Стоимость: <i>16 000 ₽</i>
            """, bot);
        }
    }

    private void sendContacts(Long chatId, TelegramLongPollingBot bot) throws TelegramApiException {
        String contactsText = """
            <b>🏕 Контакты GreenHill</b>
            
            📞 <b>Руководители:</b> 
            💚 Гульсум Рафаэловна
            <a href="tel:+79371604949">+7 (937) 160-49-49</a>
            💚 Лариса Рамилевна
            <a href="tel:+79174608201">+7 (917) 460-82-01</a>
            
           
            🌐 <b>Сайт для заявок:</b>
            👉 vk.link/greenhill_ufa
            
            🤖 <b>Бот для заявок:</b>
            👉 <a href="https://t.me/greenhill_camp_bot">@greenhill_camp_bot</a>
            
            💬 <b>Telegram-канал:</b>
            👉 <a href="https://t.me/greenhill_camp">@greenhill_camp</a>
            
            📩 <b>ВКонтакте:</b>
            👉 vk.com/greenhill_ufa
            """;

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(contactsText);
        message.setParseMode("HTML");
        bot.execute(message);
    }

    private void sendMessage(Long chatId, String text, TelegramLongPollingBot bot) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setParseMode("HTML");
        bot.execute(message);
    }

    private boolean validateBirthday(String birthday) {
        return birthday.matches("^\\d{2}\\.\\d{2}\\.\\d{4}$");
    }

    private boolean validateFullName(String fullName) {
        return fullName.matches("^[А-Яа-яЁё]+\\s+[А-Яа-яЁё]+\\s+[А-Яа-яЁё]+$");
    }

    private boolean validatePhone(String phone) {
        // Убираем все нецифровые символы кроме +
        String cleaned = phone.replaceAll("[^\\d+]", "");
        return cleaned.matches("^(7|8|\\+7)\\d{10}$");
    }

    private enum ApplicationState {
        AWAITING_BIRTHDAY,
        AWAITING_FULL_NAME,
        AWAITING_PHONE
    }
}