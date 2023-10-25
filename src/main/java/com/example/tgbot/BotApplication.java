package com.example.tgbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class BotApplication {
	// Запустіть бота і знайдіть його в телезі https://t.me/Exchange_Rate_inf_bot - силочка (CTRL + ПКМ)
	public static void main(String[] args) {
		SpringApplication.run(BotApplication.class, args);
	}
}
