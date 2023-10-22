package com.example.tgbot;

import com.example.tgbot.config.BotConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class BotApplication extends TelegramLongPollingBot {

	final BotConfig config;

	public BotApplication(BotConfig config){
		this.config = config;
	}


	@Override
	public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
           if (update.getMessage().hasText()){
			   String messageText = update.getMessage().getText();
			   long chatId = update.getMessage().getChatId();

			   switch (messageText) {
				   case "/start": // Считываем имя и отправляем приветсвтие
						   startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
						   break;
						   default: sendMessage(chatId, "Sorry, command not found");
			   }
		   }
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

	private void startCommandReceived(long chatId, String name){
		String answer = "Hi, " + name + ", nice to meet you!"; // Приветствие

		sendMessage(chatId, answer);
	}

	private void sendMessage(long chatId, String textToSend){ //Метод для отправки сообщений
		SendMessage message = new SendMessage();

		message.setChatId(String.valueOf(chatId)); //Чат ид при получении лонг при отправлении стринг
		message.setText(textToSend);

		try {
			execute(message);
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {

	}

}
