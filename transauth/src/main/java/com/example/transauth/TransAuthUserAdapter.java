package com.example.transauth;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TransAuthUserAdapter {

    public static void getUserFromYandex(String token, UserCallback callback) {
        new GetUserTask(token, callback).execute();
    }

    private static class GetUserTask extends AsyncTask<Void, Void, TransAuthUser> {
        private String token;
        private UserCallback callback;

        public GetUserTask(String token, UserCallback callback) {
            this.token = token;
            this.callback = callback;
        }

        @Override
        protected TransAuthUser doInBackground(Void... voids) {
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

                    Log.d("User", "Response: " + jsonResponse.toString());

                    // Создание объекта TransAuthUser из JSON данных
                    TransAuthUser user = TransAuth.getUser();
                    user.setLogin(jsonResponse.getString("login"));
                    user.setUsername(jsonResponse.getString("display_name"));
                    user.setEmail(jsonResponse.getString("default_email"));
                    // и так далее...

                    TransAuth.setUser(user);

                    Log.d("User", "Username: " + user.getUsername());
                    Log.d("User", "Email: " + user.getEmail());
                    Log.d("User", "Login: " + user.getLogin());

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

        @Override
        protected void onPostExecute(TransAuthUser user) {
            if (callback != null) {
                callback.onUserReceived(user);
            }
        }
    }

    public interface UserCallback {
        void onUserReceived(TransAuthUser user);
    }
}
