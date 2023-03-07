package com.josejordan.chatgptjava;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OpenAIApi {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    Call<JsonObject> sendRequest(@Header("Authorization") String authorization, @Body JsonObject postFields);
}
