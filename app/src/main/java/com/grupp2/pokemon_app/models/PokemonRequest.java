package com.grupp2.pokemon_app.models;

import java.util.ArrayList;

/**
 * Created by oskar on 2018-05-04.
 */

public class PokemonRequest {

    private ArrayList<Pokemon> results;

    //Generate getter and setter


    public ArrayList<Pokemon> getResults() {
        return results;
    }

    public void setResults(ArrayList<Pokemon> results) {
        this.results = results;
    }
}
