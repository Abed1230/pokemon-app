package com.grupp2.pokemon_app.pokeapi;

import com.grupp2.pokemon_app.models.PokemonResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PokeapiService {


    @GET("pokemon")
    Call<PokemonResponse> obtainPokemonList(@Query("limit") int limit, @Query("offset") int offset);

}