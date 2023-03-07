package com.josejordan.chatgptjava;

import static com.josejordan.chatgptjava.Constants.API_KEY;
import static com.josejordan.chatgptjava.Constants.BASE_URL;
import static com.josejordan.chatgptjava.Constants.MAX_TOKENS;
import static com.josejordan.chatgptjava.Constants.MODEL_NAME;
import static com.josejordan.chatgptjava.Constants.TEMPERATURE;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class OpenAIChatBotClient {

    private static final String TAG = OpenAIChatBotClient.class.getSimpleName();

    private final OpenAIApi openAIApi;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public OpenAIChatBotClient() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.connectTimeout(30, TimeUnit.SECONDS); // 30 segundos de tiempo de espera para establecer la conexión
        okHttpClient.readTimeout(30, TimeUnit.SECONDS); // 30 segundos de tiempo de espera para recibir datos


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

