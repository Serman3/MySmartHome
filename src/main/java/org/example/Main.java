package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Main {
    private static final String SERVER_URL = "http://localhost:9998"; // URL сервера моделирующего устройства умного дома

    public static void main(String[] args) {
        // Пример получения данных из сети
       /* String data = getDataFromNetwork();
        System.out.println("Received data: " + data);*/

        // Пример отправки данных в сеть
        sendDataToNetwork("DbMG_38BBgaI0Kv6kzGK");
    }

    private static String getDataFromNetwork() {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void sendDataToNetwork(String data) {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            connection.getOutputStream().write(Base64.getEncoder().encode(data.getBytes(StandardCharsets.UTF_8)));
            int responseCode = connection.getResponseCode();
            System.out.println("Response code: " + responseCode);

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}