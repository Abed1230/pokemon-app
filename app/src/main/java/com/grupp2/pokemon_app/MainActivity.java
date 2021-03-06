package com.grupp2.pokemon_app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.grupp2.pokemon_app.models.Pokemon;
import com.grupp2.pokemon_app.models.PokemonModel;
import com.grupp2.pokemon_app.models.PokemonResponse;
import com.grupp2.pokemon_app.pokeapi.PokeapiService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements PokemonListAdapter.PokemonAdapterListener{

    private static final String TAG = "POKEDEX";
    private Retrofit retrofit;
    private PokeapiService service;
    private RecyclerView recyclerView;
    private PokemonListAdapter pokemonListAdapter;
    private int offset;
    private boolean b;
    private SearchView searchView;
    private Dialog mDialog;
    private ProgressDialog mProgress;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDialog = new Dialog(this);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Loading");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        progressBar = findViewById(R.id.progressBar);

        pokemonListAdapter = new PokemonListAdapter(this, this);
        recyclerView.setAdapter(pokemonListAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(PokeapiService.class);

        b = true;
        offset = 0;

        obtainData(offset);

    }

    private void obtainData(int offset){
        Call<PokemonResponse> pokemonResponseCall = service.obtainPokemonList(949);
        pokemonResponseCall.enqueue(new Callback<PokemonResponse>() {

            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                b = true;
                if(response.isSuccessful()){
                    PokemonResponse pokemonResponse = response.body();
                    ArrayList<Pokemon> pokemonList = pokemonResponse.getResults();

                    pokemonListAdapter.addPokemonList(pokemonList);
                    progressBar.setVisibility(View.GONE);
                }else{
                    Log.e(TAG, " onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                b = true;
                Log.e(TAG, " onFailure: " + t.getMessage());
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

                    showDialog(pokemon);

                } else {
                    Toast.makeText(MainActivity.this, "Pokemon not found", Toast.LENGTH_SHORT).show();
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


        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.menu_main, menu);

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView = (SearchView) menu.findItem(R.id.action_search)
                    .getActionView();
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setMaxWidth(Integer.MAX_VALUE);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // filter recycler view when query submitted
                    pokemonListAdapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    // filter recycler view when text is changed
                    pokemonListAdapter.getFilter().filter(query);
                    return false;
                }
            });
            return true;
        }

        public void onPokemonSelected (Pokemon p){
            mProgress.show();
            obtainPokemon(p.getName());
        }

    private void showDialog(PokemonModel pokemon) {
        mProgress.dismiss();
        mDialog.setContentView(R.layout.popup);

        TextView nameText = mDialog.findViewById(R.id.nameTextView);
        TextView idText = mDialog.findViewById(R.id.idTextView);
        TextView weightText = mDialog.findViewById(R.id.weightTextView);
        TextView heightText = mDialog.findViewById(R.id.heightTextView);
        ImageView imageView = mDialog.findViewById(R.id.photoImageView);

        nameText.setText(pokemon.getName());
        idText.setText("ID: " + String.valueOf(pokemon.getId()));
        heightText.setText("Height: " + String.valueOf(pokemon.getHeight()) + " dm");
        weightText.setText("Weight: " + String.valueOf(pokemon.getWeight()) + " kg");

        Glide.with(this)
                .load("https://raw.githubusercontent.com/PokeAPI/sprites/968538f4/sprites/pokemon/" + pokemon.getId() + ".png")
                .centerCrop()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);


        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();

    }

    }

