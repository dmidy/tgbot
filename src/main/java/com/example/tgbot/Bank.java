package com.example.tgbot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Bank {
    @SneakyThrows
    public String getRate(String value, String bank, String sellOrBuy) {
        String rateCode = null;
        String uri = null;
        String eq = null;
        String nameBuyOrSell = null;

        if (bank.equalsIgnoreCase("privat")) {
            uri = "https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=5";
            eq = "ccy";

            if (sellOrBuy.equalsIgnoreCase("sell")) nameBuyOrSell = "sale";
            else if (sellOrBuy.equalsIgnoreCase("buy")) nameBuyOrSell = "buy";

            if (value.equalsIgnoreCase("usd")) rateCode = "USD";
            else if (value.equalsIgnoreCase("eur")) rateCode = "EUR";
        } else if (bank.equalsIgnoreCase("mono")) {
            uri = "https://api.monobank.ua/bank/currency";
            eq = "currencyCodeA";

            if (sellOrBuy.equalsIgnoreCase("sell")) nameBuyOrSell = "rateSell";
            else if (sellOrBuy.equalsIgnoreCase("buy")) nameBuyOrSell = "rateBuy";

            if (value.equalsIgnoreCase("usd")) rateCode = "840";
            else if (value.equalsIgnoreCase("eur")) rateCode = "978";
        } else if (bank.equalsIgnoreCase("nby")) {
            uri = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
            eq = "r030";

            if (sellOrBuy.equalsIgnoreCase("buy")) nameBuyOrSell = "rate";
            else System.out.println("invalid BuyOrSell");

            if (value.equalsIgnoreCase("usd")) rateCode = "840";
            else if (value.equalsIgnoreCase("eur")) rateCode = "978";
        }

        HttpRequest request = HttpRequest.newBuilder(new URI(uri))
                .header("Content-type", "application/json;charset=UTF-8")
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
        try {
            for (JsonElement element : jsonArray) {
                JsonObject currency = element.getAsJsonObject();
                if (rateCode.equals(currency.get(eq).getAsString())) {
                    return currency.get(nameBuyOrSell).getAsString();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }
}
