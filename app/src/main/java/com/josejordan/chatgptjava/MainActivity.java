package com.josejordan.chatgptjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String API_KEY = "CHATGPTKEY";
    private static final String MODEL_NAME = "gpt-3.5-turbo";
    private static final int MAX_TOKENS = 12;
    private static final int TEMPERATURE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String query = "What is the capital city of England?";

        OpenAIChatBotTask task = new OpenAIChatBotTask();
        task.execute(query);
    }

    private static class OpenAIChatBotTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... queries) {
            String query = queries[0];
            String result = "";
            try {
                URL url = new URL("https://api.openai.com/v1/chat/completions");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Authorization", "Bearer " + API_KEY);

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

                con.setDoOutput(true);
                con.getOutputStream().write(postFields.toString().getBytes(StandardCharsets.UTF_8));

                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                result = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");

            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
            }
            return result;
        }

        protected void onPostExecute(String response) {
            // Aquí puedes procesar la respuesta del bot de chat
            Log.i(TAG, "Chatbot response: " + response);
        }
    }
}
