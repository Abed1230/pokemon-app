package com.grupp2.pokemon_app.models;

import java.io.Serializable;

/**
 * Created by Abed on 05/08/2018.
 */

public class PokemonModel implements Serializable {

    private int id;
    private String name;
    private int weight;
    private int height;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
