package com.example.pokedex;

import android.content.Context;
import java.util.LinkedList;

public class Pokedex {

    int nationalNumber;
    String name;

    private Context context;

    public Pokedex(int nationalNumber, String name) {
        this.nationalNumber = nationalNumber;
        this.name = name;

    }

    public Pokedex(Context context){
        this.context = context;
    }

    public Pokedex() {
        nationalNumber = 896;
        name = "Glastrier";
    }

    public int getNumber() { return nationalNumber; }
    public boolean setNumber(int number) {
        if (number > 0 && number < 1010) {
            this.nationalNumber = number;
            return true;
        }
        return false;
    }

    public String getName() { return name; }
    public boolean setName(String name) {
        if (name.matches("[A-Za-z .]{3,12}")) {
            this.name = name;
            return true;
        }
        return false;
    } }


