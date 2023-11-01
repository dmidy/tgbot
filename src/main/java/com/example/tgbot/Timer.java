package com.example.tgbot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static jdk.javadoc.internal.tool.Main.execute;


public class Timer {
    public  void sendScheduledMessageAtNoon(long chatId, String message, Integer time, Integer minute) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Calendar currentTime = Calendar.getInstance();

        Calendar scheduledTime = Calendar.getInstance();
        scheduledTime.set(Calendar.HOUR_OF_DAY, time);
        scheduledTime.set(Calendar.MINUTE, minute);
        scheduledTime.set(Calendar.SECOND, 0);

        if (currentTime.after(scheduledTime)) {
            scheduledTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        long initialDelay = scheduledTime.getTimeInMillis() - currentTime.getTimeInMillis();

        scheduler.schedule(() -> {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(message);


            execute(String.valueOf(sendMessage));
        }, initialDelay, TimeUnit.MILLISECONDS);
    }
}
