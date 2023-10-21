package com.example.tgbot;

import com.google.gson.*;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class PrivatBank {
    private static final String RATE_URI = "https://api.privatbank.ua/p24api/pubinfo?exchange&coursid=5";

    @SneakyThrows
    public String usdGet() {

        HttpRequest request = HttpRequest.newBuilder(new URI(RATE_URI))
                .header("Content-type", "application/json;charset=UTF-8")
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

        for (JsonElement element : jsonArray) {
            JsonObject currency = element.getAsJsonObject();
            if ("USD".equals(currency.get("ccy").getAsString())) {
                String buyRate = currency.get("buy").getAsString().replaceAll("\"", "");
                String saleRate = currency.get("sale").getAsString().replaceAll("\"", "");
                return Double.parseDouble(buyRate) + " " + Double.parseDouble(saleRate);            }
        }

        return null;
    }

    @SneakyThrows
    public String euroGet() {
        HttpRequest request = HttpRequest.newBuilder(new URI(RATE_URI))
                .header("Content-type", "application/json;charset=UTF-8")
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

        for (JsonElement element : jsonArray) {
            JsonObject currency = element.getAsJsonObject();
            if ("EUR".equals(currency.get("ccy").getAsString())) {
                String buyRate = currency.get("buy").getAsString().replaceAll("\"", "");
                String saleRate = currency.get("sale").getAsString().replaceAll("\"", "");
                return Double.parseDouble(buyRate) + " " + Double.parseDouble(saleRate);            }
        }

        return null;
    }
}
