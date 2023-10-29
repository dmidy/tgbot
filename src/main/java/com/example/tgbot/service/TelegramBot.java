package com.example.tgbot.service;

import com.example.tgbot.config.BotConfig;
import com.example.tgbot.Bank;
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
            Для отримання інформації нажміть - "Отримати інформацію про валюту". \s
            Для налаштування валюти, банку та часу сповіщення - "Налаштування". \s
            Якщо Вам щось не зрозуміло, натисніть будь ласка на - "Допомога".""";
    static boolean privatBank= false;
    static boolean monoBank = true;
    static boolean nBy = false;

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
                    if (monoBank) sendBankChoiceMessage(chatId, "Обрано MonoBank.", true, false, false);
                    if (privatBank) sendBankChoiceMessage(chatId, "Обрано PrivatBank.", false, true, false);
                    if (nBy)sendBankChoiceMessage(chatId, "Обрано PrivatBank", false, false, true);
                    break;
                case "MonoBank":
                    sendBankChoiceMessage(chatId, "Обрано MonoBank.", true, false, false);
                    break;
                case "PrivatBank":
                    sendBankChoiceMessage(chatId, "Обрано PrivatBank.", false, true, false);
                    break;
                case "NBU":
                    sendBankChoiceMessage(chatId, "Обрано NBU.", false, false, true);
                    break;
                case "MonoBank✅":
                    sendBankChoiceMessage(chatId, "Обрано PrivatBank.", false, true, false);
                    break;
                case "PrivatBank✅":
                    sendBankChoiceMessage(chatId, "Обрано MonoBank.", true, false, false);
                    break;
                case "NBU✅":
                    sendBankChoiceMessage(chatId, "Обрано MonoBank.", true, false, false);
                    break;
                case "Валюта":
                    if (usdChoice) sendCurrencySelectionMessage(chatId, "Обрано USD.", true, false);
                    if (eurChoice) sendCurrencySelectionMessage(chatId, "Обрано USD і EUR.", true, true);
                    if (usdANDeur) sendCurrencySelectionMessage(chatId,"Обрано USD і EUR.", true, true);
                    break;
                case "EUR":
                    sendCurrencySelectionMessage(chatId, "Обрано USD і EUR.", true, true);
                    break;
                case "USD":
                    sendCurrencySelectionMessage(chatId, "Обрано USD і EUR.", true, true);
                    break;
                case "USD✅":
                    sendCurrencySelectionMessage(chatId,"Обрано EUR." , false, true);
                    break;
                case "EUR✅":
                    sendCurrencySelectionMessage(chatId,"Обрано USD.", true, false);
                    break;
                    case "Отримати інформацію про валюту":
                        informationMessage(chatId, "Інформація про вибір:");
                        getInformationAboutCurrency(chatId, "Курс на даний момент:");
                        break;

                case "Кількість знаків після коми":
                    if (twoAfterPoint) sendNumberAfterPoint(chatId, "Обрано 2 числа після коми.", 2);
                    if (threeAfterPoint) sendNumberAfterPoint(chatId, "Обрано 3 числа після коми.", 3);
                    if (fourAfterPoint) sendNumberAfterPoint(chatId, "Обрано 4 числа після коми.", 4);
                    break;
                case "2":
                    setNumberAfterPoint(chatId, "Обрано 2 числа після коми.", 2); // Для двух чисел после точки
                    break;
                case "3":
                    setNumberAfterPoint(chatId, "Обрано 3 числа після коми.", 3); // Для трех чисел после точки
                    break;
                case "4":
                    setNumberAfterPoint(chatId, "Обрано 4 числа після коми.", 4); // Для четырех чисел после точки
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
    private void getInformationAboutCurrency(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        Bank bank = new Bank();
        String alltext = "";
         String bankC = "";
         String currency = "";
         int num = 0;

         if (privatBank){
             bankC = "privat";
         } else if (monoBank) {
             bankC = "mono";
         }else bankC = "nby";

         if (usdChoice){
             currency = "usd";
         }else if(eurChoice){
             currency = "eur";
         }else currency = "usdAndEur";

         if (twoAfterPoint){
             num = 5;
         } else if (threeAfterPoint) {
             num = 6;
         }else num = 7;

         if (bankC.equals("nby")){
             String getValSell = bank.getRate(currency, bankC, "buy");
             String doneInfoBuy = getValSell.substring(0, num);

             alltext = "\n\nКупівля: " + doneInfoBuy;
         }else {
             String getValSell = bank.getRate(currency, bankC, "sell");
             String doneInfoSell = getValSell.substring(0, num);

             String getValBuy = bank.getRate(currency, bankC, "buy");
             String doneInfoBuy = getValBuy.substring(0, num);

             alltext = "\n\nПродаж: " + doneInfoSell + "\nКупівля: " + doneInfoBuy;
         }

        sendMessage(chatId, textToSend + alltext);

        try {
            execute(message);
        }catch (TelegramApiException ignored){
        }
    }
    private void informationMessage(long chatId, String textToSend){
        String bank = "";
        String currency = "";
        String num = "";

        if (monoBank){
            bank = "MonoBank";
        }else if (privatBank){
            bank = "PrivatBank";
        }else
            bank = "NBU";

        if (usdChoice){
            currency += "USD";
        }else if (eurChoice){
            currency = "EUR";
        }else
            currency = "USD і EUR";

        if (twoAfterPoint){
            num = "2";
        }else if (threeAfterPoint){
            num = "3";
        }else
            num = "4";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend + "\n\nБанк: " + bank + "\nВалюта: " + currency + "\nК-сть знаків після коми: " + num);

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
    private void sendCurrencySelectionMessage(long chatId, String textToSend, boolean usdSelected, boolean eurSelected) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(usdSelected ? "USD✅" : "USD");
        row.add(eurSelected ? "EUR✅" : "EUR");
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

    private void sendBankChoiceMessage(long chatId, String textToSend, boolean monoSelected, boolean privatSelected, boolean nbuSelected) {
        monoBank = monoSelected;
        privatBank = privatSelected;
        nBy = nbuSelected;

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(monoSelected ? "MonoBank✅" : "MonoBank");
        row.add(privatSelected ? "PrivatBank✅" : "PrivatBank");
        row.add(nbuSelected ? "NBU✅" : "NBU");
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
    private void sendNumberAfterPoint(long chatId, String textToSend, int numAfterPoint) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        twoAfterPoint = (numAfterPoint == 2);
        threeAfterPoint = (numAfterPoint == 3);
        fourAfterPoint = (numAfterPoint == 4);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("2" + (twoAfterPoint ? "✅" : ""));
        row.add("3" + (threeAfterPoint ? "✅" : ""));
        row.add("4" + (fourAfterPoint ? "✅" : ""));
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
    private void setNumberAfterPoint(long chatId, String textToSend, int numAfterPoint) {
        twoAfterPoint = false;
        threeAfterPoint = false;
        fourAfterPoint = false;

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("2");
        row.add("3");
        row.add("4");

        switch (numAfterPoint) {
            case 2:
                twoAfterPoint = true;
                row.set(0, "2✅");
                break;
            case 3:
                threeAfterPoint = true;
                row.set(1, "3✅");
                break;
            case 4:
                fourAfterPoint = true;
                row.set(2, "4✅");
                break;
        }

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
        row.add("Вимкнути сповіщення✅");
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