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

public class MonoBank {
    private static final String RATE_URI = "https://api.monobank.ua/bank/currency";
    @SneakyThrows
    public String usdGet(){
        HttpRequest request = HttpRequest.newBuilder(new URI(RATE_URI))
                .header("Content-type" , "application/json;charset=UTF-8")
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

        for (JsonElement element : jsonArray) {
            JsonObject currency = element.getAsJsonObject();
            if ("840".equals(currency.get("currencyCodeA").getAsString())) {
                return currency.get("rateBuy") +" "+currency.get("rateSell");
            }
        }

        return response.body();
    }
    @SneakyThrows
    public String euroGet(){
        HttpRequest request = HttpRequest.newBuilder(new URI(RATE_URI))
                .header("Content-type" , "application/json;charset=UTF-8")
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

        for (JsonElement element : jsonArray) {
            JsonObject currency = element.getAsJsonObject();
            if ("978".equals(currency.get("currencyCodeA").getAsString())) {
                return currency.get("rateBuy") +" "+currency.get("rateSell");
            }
        }

        return response.body();
    }
}
