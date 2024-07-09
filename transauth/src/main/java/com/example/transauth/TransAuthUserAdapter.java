package com.example.transauth;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TransAuthUserAdapter {

    /**
     * Получение данных пользователя из Yandex OAuth API
     * @param token токен
     * @return данные пользователя
     */
    public static TransAuthUser getUserFromYandex(String token) {
        String urlString = "https://login.yandex.ru/info?format=json";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "OAuth " + token);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) { // HTTP OK
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Преобразование ответа в объект JSON
                JSONObject jsonResponse = new JSONObject(response.toString());

                // Создание объекта TransAuthUser из JSON данных
                TransAuthUser user = new TransAuthUser();
                user.setLogin(jsonResponse.getString("login"));
                user.setUsername(jsonResponse.getString("display_name"));
                user.setEmail(jsonResponse.getString("default_email"));
                // и так далее...

                return user;
            } else {
                // Обработка ошибки
                System.out.println("Ошибка при получении данных пользователя. Код ответа: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
