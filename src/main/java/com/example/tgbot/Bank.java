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
    public String usdGet(String uri,String nameBuy,String nameSell, String rateCode, String eq){
        HttpRequest request = HttpRequest.newBuilder(new URI(uri))
                .header("Content-type" , "application/json;charset=UTF-8")
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

        for (JsonElement element : jsonArray) {
            JsonObject currency = element.getAsJsonObject();
            if (rateCode.equals(currency.get(eq).getAsString())) {
                return currency.get(nameBuy).getAsString() +" "+currency.get(nameSell).getAsString();
            }
        }

        return response.body();
    }
    @SneakyThrows
    public String euroGet(String uri,String nameBuy,String nameSell, String rateCode, String eq){
        HttpRequest request = HttpRequest.newBuilder(new URI(uri))
                .header("Content-type" , "application/json;charset=UTF-8")
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

        JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

        for (JsonElement element : jsonArray) {
            JsonObject currency = element.getAsJsonObject();
            if (rateCode.equals(currency.get(eq).getAsString())) {
                return currency.get(nameBuy).getAsString() +" "+currency.get(nameSell).getAsString();
            }
        }

        return response.body();
    }


}
