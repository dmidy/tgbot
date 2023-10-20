package com.example.tgbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class botConfig {

    @Value("${bot.name}")
    String botName;

    @Value("${bot.token}")
    String token;
}
