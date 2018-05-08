package com.grupp2.pokemon_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.grupp2.pokemon_app.models.Pokemon;
import com.grupp2.pokemon_app.models.PokemonModel;

public class PokemonActivity extends AppCompatActivity {

    private static final String TAG = "Patrik";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        Intent intent = getIntent();
        PokemonModel pokemon = (PokemonModel) intent.getSerializableExtra("pokemon");

        Log.d(TAG, "onCreate: " + pokemon.getName());
    }
}
