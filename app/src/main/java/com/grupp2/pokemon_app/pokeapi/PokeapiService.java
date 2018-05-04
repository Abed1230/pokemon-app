package com.grupp2.pokemon_app.pokeapi;

import com.grupp2.pokemon_app.models.PokemonRequest;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by oskar on 2018-05-04.
 */

public interface PokeapiService {

    @GET("pokemon")
    Call<PokemonRequest> obtenerListaPokemon();

}
