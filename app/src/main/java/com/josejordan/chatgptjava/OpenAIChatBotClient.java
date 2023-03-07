package com.josejordan.chatgptjava;
/*
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
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
                JsonObject postFields = new JsonObject();
                postFields.addProperty("model", MODEL_NAME);

                JsonArray messagesArr = new JsonArray();
                JsonObject messageObj = new JsonObject();
                messageObj.addProperty("role", "user");
                messageObj.addProperty("content", query);
                messagesArr.add(messageObj);

                postFields.add("messages", messagesArr);
                postFields.addProperty("max_tokens", MAX_TOKENS);
                postFields.addProperty("temperature", TEMPERATURE);

                RequestBody requestBody = RequestBody.create(postFields.toString(), JSON);

                Request request = new Request.Builder()
                        .url("https://api.openai.com/v1/chat/completions")
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .post(requestBody)
                        .build();


                try (Response responseHttp = client.newCall(request).execute()) {
                    if (!responseHttp.isSuccessful()) {
                        throw new IOException("Unexpected response code: " + responseHttp);
                    }

                    assert responseHttp.body() != null;
                    JsonObject jsonResponse = JsonParser.parseString(responseHttp.body().string()).getAsJsonObject();
                    response = jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();

                } catch (IOException e) {
                    Log.e(TAG, "Error parsing JSON response", e);
                }


            } catch (Exception e) {
                Log.e(TAG, "Error creating JSON object", e);
            }
            String finalResponse = response;
            handler.post(() -> listener.onResponse(finalResponse));
        });
    }

}*/

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenAIChatBotClient {

    private static final String TAG = OpenAIChatBotClient.class.getSimpleName();
    private static final String BASE_URL = "https://api.openai.com/v1/";
    private static final String API_KEY = "API_KEY";
    private static final String MODEL_NAME = "gpt-3.5-turbo";
    private static final int MAX_TOKENS = 12;
    private static final int TEMPERATURE = 0;
    private final OpenAIApi openAIApi;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public OpenAIChatBotClient() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        openAIApi = retrofit.create(OpenAIApi.class);
    }

    public void sendRequest(String query, OnResponseListener listener) {
        JsonObject postFields = new JsonObject();
        postFields.addProperty("model", MODEL_NAME);

        JsonArray messagesArr = new JsonArray();
        JsonObject messageObj = new JsonObject();
        messageObj.addProperty("role", "user");
        messageObj.addProperty("content", query);
        messagesArr.add(messageObj);

        postFields.add("messages", messagesArr);
        postFields.addProperty("max_tokens", MAX_TOKENS);
        postFields.addProperty("temperature", TEMPERATURE);

        Call<JsonObject> call = openAIApi.sendRequest("Bearer " + API_KEY, postFields);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonResponse = response.body();

                    assert jsonResponse != null;
                    String text = jsonResponse.getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content")
                            .getAsString();
                    handler.post(() -> listener.onResponse(text));

                } else {
                    Log.e(TAG, "Error response: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage(), t);
            }
        });
    }
}

