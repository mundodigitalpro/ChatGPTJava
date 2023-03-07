package com.josejordan.chatgptjava;

import static android.view.View.VISIBLE;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private EditText etText;
    private TextView tvText;
    private OpenAIChatBotClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etText = findViewById(R.id.etText);
        tvText = findViewById(R.id.tvText);
        client = new OpenAIChatBotClient();


        findViewById(R.id.btnSend).setOnClickListener(view -> {
            String query = etText.getText().toString();
            if (!query.isEmpty()) {
                client.sendRequest(query, response -> {
                    // Aquí puedes procesar la respuesta del bot de chat
                    Log.i(TAG, "Chatbot response: " + response);
                    tvText.setVisibility(VISIBLE);
                    tvText.setTextColor(Color.WHITE);
                    tvText.setText(response);
                    etText.setText("");
                });
            }
        });
    }
}
