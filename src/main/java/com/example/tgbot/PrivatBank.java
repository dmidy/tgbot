package com.example.tgbot;

import com.google.gson.*;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class PrivatBank {
    Bank bank = new Bank();
    public String usdGet(){
        return bank.usdGet("https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=5"
                ,"buy"
                ,"sale"
                ,"USD"
                ,"ccy");
    }
    public String euroGet(){
        return bank.euroGet("https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=5"
        ,"buy"
        ,"sale"
        ,"USD"
        ,"ccy");
    }
}
