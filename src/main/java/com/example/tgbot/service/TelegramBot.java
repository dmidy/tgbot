package com.example.tgbot.service;

import com.example.tgbot.config.BotConfig;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;
    static final String HELP_TEXT = """
            This bot is created to demonstrate Spring bot.\s

            You can execute commands from the main menu ont the left or by typing a command:\s

            Type /start.
            You will se a welcome message.""";
    static final String SETTINGS_TEXT = "Будь ласка виберіть параметри, які для вас потрібні.";
    static final String START_TEXT = """
            Цей бот призначений для моніторингу за валютой.\s
                        
            Ви можете обрати банк, валюту та зручний для час, коли ми Вас будемо сповіщати про теперішній курс валют за допомогою налаштувань. \s
                        
            Для зручності ми уже обрали стандартні пареметри, тому можете спробувати нажати на кнопку для отримання інформації.""";

    static final String STEP_BACK_TEXT = """
            Для отримання інформації нажміть - \"Отримати інформацію про валюту\". \s
            Для налаштування валюти, банку та часу сповіщення - \"Налаштування\". \s
            Якщо Вам щось не зрозуміло, натисніть будь ласка на - \"Допомога\".""";

    static boolean privatBank= false;
    static boolean monoBank = true;
    static boolean nBU = false;

    static boolean twoAfterPoint = true;
    static boolean threeAfterPoint = false;
    static boolean fourAfterPoint = false;

    static boolean usdChoice = true;
    static boolean eurChoice = false;
    static boolean usdANDeur = false;

    public TelegramBot(BotConfig config){
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/help","info how to use this bot"));
        listOfCommands.add(new BotCommand("/settings","set your preferences"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
    @Override
    public String getBotToken(){
        return config.getToken();
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            String massageTest = update.getMessage().getText();

            long chatId = update.getMessage().getChatId();

            switch (massageTest){
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    startMenu(chatId, START_TEXT);
                    break;

                case "/help", "Допомога":
                    sendMessage(chatId, HELP_TEXT);
                    break;

                case "/settings", "Налаштування":
                    settingsMenu(chatId, SETTINGS_TEXT);
                    break;

                case "Назад":
                    startMenu(chatId, STEP_BACK_TEXT);
                    break;
                case "Назад до налаштувань":
                    settingsMenu(chatId, "🧐");
                    break;

                case "Банк":
                    if (monoBank == true) bankChoiceMono(chatId, "Обрано MonoBank.");
                    if (privatBank == true) bankChoicePrivat(chatId, "Обрано PrivatBank.");
                    if (nBU == true)bankChoiceNBU(chatId, "Обрано PrivatBank");
                    break;
                case "MonoBank":
                    bankChoiceMono(chatId, "Обрано MonoBank.");
                    break;
                case "PrivatBank":
                    bankChoicePrivat(chatId, "Обрано PrivatBank.");
                    break;
                case "NBU":
                    bankChoiceNBU(chatId, "Обрано NBU.");
                    break;

                case "Валюта":
                    if (usdChoice == true) currencySelectionUSD(chatId, "Обрано USD.");
                    if (eurChoice == true) currencySelectionEUR(chatId, "Обрано USD і EUR.");
                    if (usdANDeur == true) currencySelectionUSDandEUR(chatId,"Обрано USD і EUR.");
                    break;
                case "EUR":
                    currencySelectionUSDandEUR(chatId, "Обрано USD і EUR.");
                    break;
                case "USD":
                    currencySelectionUSDandEUR(chatId, "Обрано USD і EUR.");
                    break;
                case "USD✅":
                    currencySelectionEUR(chatId,"Обрано EUR." );
                    break;
                case "EUR✅":
                    currencySelectionUSD(chatId,"Обрано USD.");
                    break;

                case "Кількість знаків після коми":
                    if (twoAfterPoint == true)twoNumberAfterPoint(chatId, "Обрано 2 числа після коми.");
                    if (threeAfterPoint == true)threeNumberAfterPoint(chatId, "Обрано 3 числа після коми.");
                    if (fourAfterPoint == true)fourNumberAfterPoint(chatId, "Обрано 4 числа після коми.");
                    break;
                case "2":
                    twoNumberAfterPoint(chatId, "Обрано 2 числа після коми.");
                    break;
                case "3":
                    threeNumberAfterPoint(chatId, "Обрано 3 числа після коми.");
                    break;
                case "4":
                    fourNumberAfterPoint(chatId, "Обрано 4 числа після коми.");
                    break;

                case "Час сповіщення":
                    notificationTimer(chatId, "Виберіть час для сповіщень.");
                    break;
                default: sendMessage(chatId, "sorry command was not recognised");
            }
        }
    }
    private void startCommandReceived(long chatId, String name){
        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + "\uD83E\uDEF6\uD83C\uDFFB") ;
        sendMessage(chatId, answer);
    }
    private void sendMessage(long chatId,String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        }catch (TelegramApiException ignored){
        }
    }

    private void startMenu(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("Отримати інформацію про валюту");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("Налаштування");
        row.add("Допомога");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        }catch (TelegramApiException ignored){
        }
    }

    private void settingsMenu(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Кількість знаків після коми");
        row.add("Банк");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Валюта");
        row.add("Час сповіщення");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад");
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        }catch (TelegramApiException ignored){
        }
    }
    private void currencySelectionUSD(long chatId, String textToSend){
        usdChoice = true;
        eurChoice = false;
        usdANDeur = false;
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("USD✅");
        row.add("EUR");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад до налаштувань");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }
    private void currencySelectionEUR(long chatId, String textToSend){
        usdChoice = false;
        eurChoice = true;
        usdANDeur = false;
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("USD");
        row.add("EUR✅");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад до налаштувань");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }private void currencySelectionUSDandEUR(long chatId, String textToSend){
        usdChoice = false;
        eurChoice = false;
        usdANDeur = true;
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("USD✅");
        row.add("EUR✅");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад до налаштувань");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }

    private void bankChoiceMono(long chatId, String textToSend){
        monoBank = true;
        privatBank = false;
        nBU = false;
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("MonoBank✅");
        row.add("PrivatBank");
        row.add("NBU");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад до налаштувань");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        }catch (TelegramApiException ignored){
        }
    }
    private void bankChoicePrivat(long chatId, String textToSend){
        privatBank = true;
        monoBank = false;
        nBU = false;
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("MonoBank");
        row.add("PrivatBank✅");
        row.add("NBU");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад до налаштувань");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        }catch (TelegramApiException ignored){
        }
    }
    private void bankChoiceNBU(long chatId, String textToSend){
        nBU = true;
        privatBank = false;
        monoBank = false;

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("MonoBank");
        row.add("PrivatBank");
        row.add("NBU✅");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад до налаштувань");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        }catch (TelegramApiException ignored){
        }
    }
    private void twoNumberAfterPoint(long chatId, String textToSend){
        threeAfterPoint = false;
        fourAfterPoint = false;
        twoAfterPoint = true;


        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("2✅");
        row.add("3");
        row.add("4");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад до налаштувань");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        }catch (TelegramApiException ignored){
        }
    }
    private void threeNumberAfterPoint(long chatId, String textToSend){
        twoAfterPoint = false;
        threeAfterPoint = true;
        fourAfterPoint = false;
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("2");
        row.add("3✅");
        row.add("4");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад до налаштувань");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        }catch (TelegramApiException ignored){
        }
    }
    private void fourNumberAfterPoint(long chatId, String textToSend){
        twoAfterPoint = false;
        threeAfterPoint = false;
        fourAfterPoint = true;
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("2");
        row.add("3");
        row.add("4✅");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад до налаштувань");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        }catch (TelegramApiException ignored){
        }
    }

    private void notificationTimer(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("9");
        row.add("10");
        row.add("11");
        row.add("12");
        row.add("13");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("14");
        row.add("15");
        row.add("16");
        row.add("17");
        row.add("18");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Вимкнути сповіщення");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад до налаштувань");
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        }catch (TelegramApiException ignored){
        }
    }

}