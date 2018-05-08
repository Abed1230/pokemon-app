package com.grupp2.pokemon_app;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
    private int number;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        number = intent.getIntExtra("number", 0);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        pokemonListAdapter = new PokemonListAdapter(this);
        recyclerView.setAdapter(pokemonListAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(dy > 0){
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if(b){
                        if((visibleItemCount + pastVisibleItems) >= totalItemCount){
                            Log.i(TAG, " Last one.");

                            b = false;
                            offset +=200;
                            obtainData(offset);
                        }
                    }
                }
            }
        });

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
        Call<PokemonResponse> pokemonResponseCall = service.obtainPokemonList(20, offset);

        pokemonResponseCall.enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                b = true;
                if(response.isSuccessful()){
                    PokemonResponse pokemonResponse = response.body();
                    ArrayList<Pokemon> pokemonList = pokemonResponse.getResults();
                    pokemonListAdapter.addPokemonList(pokemonList);
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

    private void obtainPokemon(String name) {
        Call<PokemonModel> pokemonResponseCall = service.obtainPokemon(name);
        pokemonResponseCall.enqueue(new Callback<PokemonModel>() {
            @Override
            public void onResponse(Call<PokemonModel> call, Response<PokemonModel> response) {
                if (response.isSuccessful()) {
                    PokemonModel pokemon = response.body();
                    String s = "id: " + pokemon.getId() + " name: " + pokemon.getName() +
                            " height: " + pokemon.getHeight() + " weight: " + pokemon.getWeight();
                    Toast.makeText(MainActivity.this, "Pokemon: \n" + s, Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, " onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonModel> call, Throwable t) {
                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    public void onPokemonSelected(Pokemon p) {

    }
}
