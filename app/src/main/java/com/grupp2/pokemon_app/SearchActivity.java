package com.grupp2.pokemon_app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
    private Dialog mDialog;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Button btnAll = findViewById(R.id.btnAll);
        editText = findViewById(R.id.editText);
        mDialog = new Dialog(this);
        mProgress = new ProgressDialog(this);

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
                if(userInput.length() == 0) {
                    Toast.makeText(SearchActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                } else {
                    obtainPokemon(userInput);
                    mProgress.setMessage("Loading");
                    mProgress.show();
                }
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

                    showDialog(pokemon);


                } else {
                    Toast.makeText(SearchActivity.this, "Pokemon not found", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, " onResponse: " + response.errorBody());
                    mProgress.dismiss();
                }
            }
            @Override
            public void onFailure(Call<PokemonModel> call, Throwable t) {
                Log.e(TAG, " onFailure: " + t.getMessage());
                mProgress.dismiss();
            }
        });
    }

    private void showDialog(PokemonModel pokemon) {
        mDialog.setContentView(R.layout.popup);

        PokemonModel currentPokemon = pokemon;

        TextView nameText = mDialog.findViewById(R.id.nameTextView);
        TextView idText = mDialog.findViewById(R.id.idTextView);
        TextView weightText = mDialog.findViewById(R.id.weightTextView);
        TextView heightText = mDialog.findViewById(R.id.heightTextView);
        ImageView imageView = mDialog.findViewById(R.id.photoImageView);

        nameText.setText(pokemon.getName());
        idText.setText("ID: " + String.valueOf(pokemon.getId()));
        heightText.setText("Height: " + String.valueOf(pokemon.getHeight()) + "dm");
        weightText.setText("Weight: " + String.valueOf(pokemon.getWeight()) + "kg");

        Glide.with(this)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/968538f4/sprites/pokemon/" + pokemon.getId() + ".png")
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);


        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();
        mProgress.dismiss();

    }


}
