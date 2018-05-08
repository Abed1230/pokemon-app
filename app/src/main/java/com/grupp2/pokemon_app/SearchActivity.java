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
import android.widget.Toast;

import com.grupp2.pokemon_app.models.Pokemon;
import com.grupp2.pokemon_app.models.PokemonModel;
import com.grupp2.pokemon_app.pokeapi.PokeapiService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "Patrik";
    EditText editText;
    private PokeapiService service;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Button btnAll = findViewById(R.id.btnAll);
        editText = findViewById(R.id.editText);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(PokeapiService.class);

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
                String userInput = editText.getText().toString().toLowerCase();
                obtainPokemon(userInput);
            }
        });

    }

    private void obtainPokemon(final String name) {
        Call<PokemonModel> pokemonResponseCall = service.obtainPokemon(name);
        pokemonResponseCall.enqueue(new Callback<PokemonModel>() {
            @Override
            public void onResponse(Call<PokemonModel> call, Response<PokemonModel> response) {
                if (response.isSuccessful()) {
                    PokemonModel pokemon = response.body();
                    String s = "id: " + pokemon.getId() + " name: " + pokemon.getName() +
                            " height: " + pokemon.getHeight() + " weight: " + pokemon.getWeight();
                    Toast.makeText(SearchActivity.this, s, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(SearchActivity.this, PokemonActivity.class);
                    intent.putExtra("pokemon", pokemon);
                    startActivity(intent);

                } else {
                    Toast.makeText(SearchActivity.this, "Pokemon not found", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, " onResponse: " + response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<PokemonModel> call, Throwable t) {
                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });
    }

}
