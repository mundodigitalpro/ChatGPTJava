package com.josejordan.chatgptjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String query = "What is the capital city of England?";
        OpenAIChatBotClient client = new OpenAIChatBotClient();
        client.sendRequest(query, response -> {
            // Aquí puedes procesar la respuesta del bot de chat
            Log.i(TAG, "Chatbot response: " + response);
        });
    }
}
