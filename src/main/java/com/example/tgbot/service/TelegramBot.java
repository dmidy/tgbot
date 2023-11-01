package com.example.tgbot.service;

import com.example.tgbot.Timer;
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

import java.util.*;


@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;
    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }
    final String HELP_TEXT = """
            This bot is created to demonstrate Spring bot.\s

            You can execute commands from the main menu ont the left or by typing a command:\s

            Type /start.
            You will se a welcome message.""";
    final String SETTINGS_TEXT = "Будь ласка виберіть параметри, які для вас потрібні.";
    final String START_TEXT = """
            Цей бот призначений для моніторингу за валютой.\s
                        
            Ви можете обрати банк, валюту та зручний для час, коли ми Вас будемо сповіщати про теперішній курс валют за допомогою налаштувань. \s
                        
            Для зручності ми уже обрали стандартні пареметри, тому можете спробувати нажати на кнопку для отримання інформації.""";

    final String STEP_BACK_TEXT = """
            Для отримання інформації нажміть - "Отримати інформацію про валюту". \s
            Для налаштування валюти, банку та часу сповіщення - "Налаштування". \s
            Якщо Вам щось не зрозуміло, натисніть будь ласка на - "Допомога".""";
    boolean privatBank = false;
    boolean monoBank = true;
    boolean nBy = false;

    boolean twoAfterPoint = true;
    boolean threeAfterPoint = false;
    boolean fourAfterPoint = false;

    boolean usdANDeur = true;
    boolean usdChoice = true;
    boolean eurChoice = true;
    boolean nine = false;
    boolean ten = false;
    boolean eleven = false;
    boolean twelve = false;
    boolean thirteen = false;
    boolean fourteen = false;
    boolean fifteen = false;
    boolean sixteen = false;
    boolean seventeen = false;
    boolean eighteen = false;

    boolean notificationsEnabled = true;

    private Integer notificationHour = null;


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String massageTest = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (massageTest) {
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
                    if (nBy) sendBankChoiceMessage(chatId, "Обрано PrivatBank", false, false, true);
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
                    if (usdANDeur) {
                        sendCurrencySelectionMessage(chatId, "Обрано USD і EUR.", true, true, true);
                    }else if (usdChoice) {
                        sendCurrencySelectionMessage(chatId, "Обрано USD.", true, false, false);
                    }
                    else sendCurrencySelectionMessage(chatId, "Обрано EUR.", false, true, false);

                    break;
                case "EUR":
                    sendCurrencySelectionMessage(chatId, "Обрано USD і EUR.", true, true, true);
                    break;
                case "USD":
                    sendCurrencySelectionMessage(chatId, "Обрано USD і EUR.", true, true, true);
                    break;
                case "USD✅":
                    sendCurrencySelectionMessage(chatId, "Обрано EUR.", false, true, false);
                    break;
                case "EUR✅":
                    sendCurrencySelectionMessage(chatId, "Обрано USD.", true, false, false);
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
                    setNumberAfterPoint(chatId, "Обрано 2 числа після коми.", 2);
                    break;
                case "3":
                    setNumberAfterPoint(chatId, "Обрано 3 числа після коми.", 3);
                    break;
                case "4":
                    setNumberAfterPoint(chatId, "Обрано 4 числа після коми.", 4);
                    break;

                case "Час сповіщення":
                    notificationTimer(chatId, "Оберіть час сповіщення", false,false,false,false,
                            false,false,false,false,false,false, true);
                    break;
                case "9":
                    setNotificationHour(chatId, 9);
                    notificationTimer(chatId, "Оберіть час сповіщення", true,false,false,false,
                            false,false,false,false,false,false, false);
                    break;
                case "10":
                    setNotificationHour(chatId, 10);
                    notificationTimer(chatId, "Оберіть час сповіщення", false,true,false,false,
                            false,false,false,false,false,false, false);
                    break;
                case "11":
                    setNotificationHour(chatId, 11);
                    notificationTimer(chatId, "Оберіть час сповіщення", false,false,true,false,
                            false,false,false,false,false,false, false);
                    break;
                case "12":
                    setNotificationHour(chatId, 12);
                    notificationTimer(chatId, "Оберіть час сповіщення", false,false,false,true,
                            false,false,false,false,false,false, false);
                    break;
                case "13":
                    setNotificationHour(chatId, 13);
                    notificationTimer(chatId, "Оберіть час сповіщення", false,false,false,false,
                            true,false,false,false,false,false, false);
                    break;
                case "14":
                    setNotificationHour(chatId, 14);
                    notificationTimer(chatId, "Оберіть час сповіщення", false,false,false,false,
                            false,true,false,false,false,false, false);
                    break;
                case "15":
                    setNotificationHour(chatId, 15);
                    notificationTimer(chatId, "Оберіть час сповіщення", false,false,false,false,
                            false,false,true,false,false,false, false);
                    break;
                case "16":
                    setNotificationHour(chatId, 16);
                    notificationTimer(chatId, "Оберіть час сповіщення", false,false,false,false,
                            false,false,false,true,false,false, false);
                    break;
                case "17":
                    setNotificationHour(chatId, 17);
                    notificationTimer(chatId, "Оберіть час сповіщення", false,false,false,false,
                            false,false,false,false,true,false, false);
                    break;
                case "18":
                    setNotificationHour(chatId, 18);
                    notificationTimer(chatId, "Оберіть час сповіщення", false,false,false,false,
                            false,false,false,false,false,true, false);
                    break;
                case "Вимкнути сповіщення✅":
                    notificationTimer(chatId, "Оберіть час сповіщення", false,false,false,false,
                            false,false,false,false,false,false, false);
                    toggleNotifications(chatId);
                    break;
                case "Вимкнути сповіщення":
                    notificationTimer(chatId, "Оберіть час сповіщення", false,false,false,false,
                            false,false,false,false,false,false, true);
                    toggleNotifications(chatId);
                    break;
                default:
                    sendMessage(chatId, "Ой, щось не так");
            }
        }
    }

    private void toggleNotifications(long chatId) {
        notificationsEnabled = !notificationsEnabled;

        if (notificationsEnabled) {
            sendMessage(chatId, "Автоматичні сповіщення увімкнуті.");
            Timer timer = new Timer();
            getInformationAboutCurrency(chatId, "Курс на даний момент:");
            timer.sendScheduledMessageAtNoon(chatId,"alltext",14,53);
        } else {
            sendMessage(chatId, "Автоматичні сповіщення вимкнуті.");
        }
    }

    private void setNotificationHour(long chatId, int hour) {
        if (hour >= 9 && hour <= 18) {
            notificationHour = hour;
            sendMessage(chatId, "Годину для автоматичних сповіщень встановлено на " + hour + ":00.");
        } else {
            sendMessage(chatId, "Виберіть годину в діапазоні від 9 до 18.");
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + "\uD83E\uDEF6\uD83C\uDFFB");
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }

    private void startMenu(long chatId, String textToSend) {
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
        } catch (TelegramApiException ignored) {
        }
    }
    String alltext;
    protected void getInformationAboutCurrency(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        Bank bank = new Bank();
        alltext = "";
        String bankC = "";
        String currency = "";

        if (privatBank) {
            bankC = "privat";
        } else if (monoBank) {
            bankC = "mono";
        } else {
            bankC = "nby";
        }

        if (usdChoice) {
            currency = "usd";
        } else if (eurChoice) {
            currency = "eur";
        } else if(usdANDeur) {
            currency = "usdAndEur";
        }

        int decimalPlaces = twoAfterPoint ? 2 : (threeAfterPoint ? 3 : 4);

        if (bankC.equals("nby")) {
            if (usdANDeur) {
                String getValBuyUsd = bank.getRate("usd", bankC, "buy");
                String getValBuyEur = bank.getRate("eur", bankC, "buy");
                String doneInfoBuyUsd = String.format("%." + decimalPlaces + "f", Double.parseDouble(getValBuyUsd));
                String doneInfoBuyEur = String.format("%." + decimalPlaces + "f", Double.parseDouble(getValBuyEur));

                alltext = "\n\nКупівля USD: " + doneInfoBuyUsd + "\nКупівля EURO: " + doneInfoBuyEur;
            }else if (usdChoice||eurChoice) {
                String getValBuy = bank.getRate(currency, bankC, "buy");
                String doneInfoBuyUsd = String.format("%." + decimalPlaces + "f", Double.parseDouble(getValBuy));
                alltext = "\n\nКупівля: " + doneInfoBuyUsd;
            }


        } else {

            if (usdANDeur) {
                String getValSellUsd = bank.getRate("usd", bankC, "sell");
                String doneInfoSellUsd = String.format("%." + decimalPlaces + "f", Double.parseDouble(getValSellUsd));

                String getValBuyUsd = bank.getRate("usd", bankC, "buy");
                String doneInfoBuyUsd = String.format("%." + decimalPlaces + "f", Double.parseDouble(getValBuyUsd));

                //_________Eur________

                String getValSellEur = bank.getRate("eur", bankC, "sell");
                String doneInfoSellEur = String.format("%." + decimalPlaces + "f", Double.parseDouble(getValSellEur));

                String getValBuyEur = bank.getRate("eur", bankC, "buy");
                String doneInfoBuyEur = String.format("%." + decimalPlaces + "f", Double.parseDouble(getValBuyEur));

                alltext = "\n\nПродаж USD: " + doneInfoSellUsd + "\nКупівля USD: " + doneInfoBuyUsd
                        + "\n\nПродаж EURO: " + doneInfoSellEur + "\nКупівля EURO: " + doneInfoBuyEur;

            } else if (usdChoice||eurChoice) {
                String getValSell = bank.getRate(currency, bankC, "sell");
                String doneInfoSell = String.format("%." + decimalPlaces + "f", Double.parseDouble(getValSell));

                String getValBuy = bank.getRate(currency, bankC, "buy");
                String doneInfoBuy = String.format("%." + decimalPlaces + "f", Double.parseDouble(getValBuy));
                alltext = "\n\nПродаж : " + doneInfoSell + "\nКупівля: " + doneInfoBuy;
            }


        }

        sendMessage(chatId, textToSend + alltext);

        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }

    private void informationMessage(long chatId, String textToSend) {
        String bank = "";
        String currency = "";
        String num = "";

        if (monoBank) {
            bank = "MonoBank";
        } else if (privatBank) {
            bank = "PrivatBank";
        } else
            bank = "NBU";

        if (usdANDeur) {
            currency += "USD і EUR";
        } else if (eurChoice) {
            currency = "EUR";
        } else
            currency = "USD";

        if (twoAfterPoint) {
            num = "2";
        } else if (threeAfterPoint) {
            num = "3";
        } else
            num = "4";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend + "\n\nБанк: " + bank + "\nВалюта: " + currency + "\nК-сть знаків після коми: " + num);

        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }

    private void settingsMenu(long chatId, String textToSend) {
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
        } catch (TelegramApiException ignored) {
        }
    }

    private void sendCurrencySelectionMessage(long chatId, String textToSend, boolean usdSelected, boolean eurSelected, boolean usdAndEuroSelect) {
        usdChoice = usdSelected;
        eurChoice = eurSelected;
        usdANDeur = usdAndEuroSelect;

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

    private void notificationTimer(long chatId, String textToSend,
                                   boolean nineC, boolean tenC, boolean elevenC, boolean twelveC, boolean thirteenC,
                                   boolean fourteenC, boolean fifteenC, boolean sixteenC, boolean seventeenC, boolean eighteenC, boolean notificationsEnabledC) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        notificationsEnabled = notificationsEnabledC;
        nine = nineC;
        ten =tenC;
        eleven = elevenC;
        twelve = twelveC;
        thirteen = thirteenC;
        fourteen = fourteenC;
        fifteen = fifteenC;
        sixteen= sixteenC;
        seventeen = seventeenC;
        eighteen = eighteenC;
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("9" + (nine ? "✅" : ""));
        row.add("10" +(ten ? "✅" : ""));
        row.add("11" +(eleven  ? "✅" : ""));
        row.add("12" +(twelve  ? "✅" : ""));
        row.add("13" +(thirteen  ? "✅" : ""));
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("14" +(fourteen  ? "✅" : ""));
        row.add("15" +(fifteen  ? "✅" : ""));
        row.add("16" +(sixteen  ? "✅" : ""));
        row.add("17" +(seventeen  ? "✅" : ""));
        row.add("18" +(eighteen  ? "✅" : ""));
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Вимкнути сповіщення" + (notificationsEnabledC ? "✅" : ""));
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
}