package com.grupp2.pokemon_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "Patrik";
    EditText editText;
    String userInput;
    int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Button btnAll = findViewById(R.id.btnAll);
        editText = findViewById(R.id.editText);

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
            }
        });

        editText = findViewById(R.id.editText);
        Button btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInput = editText.getText().toString();

                switch ( userInput ) {
                    case "bulbasaur" :
                        number = 0;
                        break;
                    case "ivysaur" :
                        number = 1;
                        break;
                    case "venusaur" :
                        number = 2;
                        break;
                }

                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra("number", number);
                startActivity(intent);
            }
        });

    }


}
