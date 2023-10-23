package com.example.tgbot;

public class MonoBank extends Bank {
    Bank bank = new Bank();
    public String usdGet() {
        return bank.usdGet("https://api.monobank.ua/bank/currency"
                , "rateBuy"
                , "rateSell"
                , "840"
                , "currencyCodeA");
    }
    public String euroGet(){
        return bank.euroGet("https://api.monobank.ua/bank/currency"
        ,"rateBuy"
        ,"rateSell"
        ,"978"
        ,"currencyCodeA");
    }
}
