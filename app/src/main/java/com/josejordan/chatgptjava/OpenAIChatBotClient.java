package com.josejordan.chatgptjava;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAIChatBotClient {

    private static final String TAG = OpenAIChatBotClient.class.getSimpleName();
    private static final String API_KEY = "CHATGPTAPIKEY";
    private static final String MODEL_NAME = "gpt-3.5-turbo";
    private static final int MAX_TOKENS = 12;
    private static final int TEMPERATURE = 0;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void sendRequest(String query, OnResponseListener listener) {
        executor.execute(() -> {
            String response = "";
            try {
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

                RequestBody requestBody = RequestBody.create(JSON, postFields.toString());
                Request request = new Request.Builder()
                        .url("https://api.openai.com/v1/chat/completions")
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .post(requestBody)
                        .build();

                try (Response responseHttp = client.newCall(request).execute()) {
                    if (!responseHttp.isSuccessful()) {
                        throw new IOException("Unexpected response code: " + responseHttp);
                    }
                    JSONObject jsonResponse = new JSONObject(responseHttp.body().string());
                    response = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "Error parsing JSON response", e);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error creating JSON object", e);
            }

            String finalResponse = response;
            handler.post(() -> listener.onResponse(finalResponse));
        });
    }

}
