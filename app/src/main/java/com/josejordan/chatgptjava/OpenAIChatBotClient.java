package com.josejordan.chatgptjava;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class OpenAIChatBotClient {

    private static final String TAG = OpenAIChatBotClient.class.getSimpleName();
    private static final String API_KEY = "CHATGPTAPIKEY";
    private static final String MODEL_NAME = "gpt-3.5-turbo";
    private static final int MAX_TOKENS = 12;
    private static final int TEMPERATURE = 0;

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void sendRequest(String query, OnResponseListener listener) {
        executor.execute(() -> {
            String response = "";
            try {
                URL url = new URL("https://api.openai.com/v1/chat/completions");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + API_KEY);

                JSONObject postFields = new JSONObject();
                postFields.put("model", MODEL_NAME);

                List<JSONObject> messagesList = new ArrayList<>();
                JSONObject messageObj = new JSONObject();
                messageObj.put("role", "user");
                messageObj.put("content", query);
                messagesList.add(messageObj);

                JSONArray messagesArr = new JSONArray(messagesList);
                postFields.put("messages", messagesArr);
                postFields.put("max_tokens", MAX_TOKENS);
                postFields.put("temperature", TEMPERATURE);

                connection.setDoOutput(true);
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8))) {
                    writer.write(postFields.toString());
                }

                int responseCode = connection.getResponseCode();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder responseBuilder = new StringBuilder();
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null) {
                        responseBuilder.append(inputLine);
                    }
                    JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
                    response = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON response", e);
                }
                connection.disconnect();
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error sending request to OpenAI ChatBot API", e);
            }
            String finalResponse = response;
            handler.post(() -> listener.onResponse(finalResponse));
        });
    }

}
