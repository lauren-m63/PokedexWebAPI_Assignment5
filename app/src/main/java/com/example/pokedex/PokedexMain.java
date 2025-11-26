package com.example.pokedex;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class PokedexMain extends AppCompatActivity {

    EditText nationalNumberInput;
    EditText nameInput;

    Button submitButton;
    Button resetButton;
    Button dataButton;
    Button pokemonDisplayButton;

    Pokedex pokedex;

    boolean atLeastOneFilled(EditText... fields) {
        for (EditText field : fields) {
            if (!field.getText().toString().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    boolean isNumeric(String s) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.table);

        AndroidNetworking.initialize(getApplicationContext());
        pokedex = new Pokedex(this);

        nationalNumberInput = findViewById(R.id.nationalNumberInput);
        nameInput = findViewById(R.id.nameInput);
        submitButton = findViewById(R.id.submitButton);
        resetButton = findViewById(R.id.resetButton);
        dataButton = findViewById(R.id.numberButton);
        pokemonDisplayButton = findViewById(R.id.pokemonDisplayButton);

        submitButton.setOnClickListener(v -> handleSubmit());
        resetButton.setOnClickListener(v -> {
            nationalNumberInput.setText("896");
            nameInput.setText("Glastrier");
        });
        dataButton.setOnClickListener(v -> startActivity(new Intent(this, DatabaseView.class)));
        pokemonDisplayButton.setOnClickListener(v -> {
            String nameOrId = nameInput.getText().toString().trim();
            if (!nameOrId.isEmpty()) fetchPokemonFromAPI(nameOrId);
            else Toast.makeText(this, "Please enter a Pokémon name or number", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleSubmit() {
        if (!atLeastOneFilled(nationalNumberInput, nameInput)) {
            Toast.makeText(this, "Please enter a Pokémon name OR number.", Toast.LENGTH_SHORT).show();
            return;
        }

        String numText = nationalNumberInput.getText().toString().trim();
        Integer number = null;
        if (!numText.isEmpty()) {
            if (!isNumeric(numText)) {
                Toast.makeText(this, "National Number must contain digits only.", Toast.LENGTH_SHORT).show();
                return;
            }
            number = Integer.parseInt(numText);
        }

        String name = nameInput.getText().toString().trim();
        if (name.isEmpty()) name = null;

        StringBuilder fixIt = new StringBuilder();
        if (number != null && !pokedex.setNumber(number)) fixIt.append("National Number, ");
        if (name != null && !pokedex.setName(name)) fixIt.append("Name, ");
        if (fixIt.length() > 0) {
            fixIt.setLength(fixIt.length() - 2);
            Toast.makeText(this, "The following fields are not within bounds: " + fixIt, Toast.LENGTH_LONG).show();
            return;
        }

        if (number != null) {
            Cursor cursor = getContentResolver().query(
                    PokedexContentProvider.CONTENT_URI,
                    null,
                    PokedexContentProvider.COL_NATIONALNUMBER + "=?",
                    new String[]{String.valueOf(number)},
                    null
            );
            if (cursor != null && cursor.getCount() > 0) {
                Toast.makeText(this, "A Pokémon with this National Number already exists.", Toast.LENGTH_LONG).show();
                cursor.close();
                return;
            }
            if (cursor != null) cursor.close();
        }

        ContentValues values = new ContentValues();
        if (number != null) values.put(PokedexContentProvider.COL_NATIONALNUMBER, number);
        if (name != null) values.put(PokedexContentProvider.COL_NAME, name);

        getContentResolver().insert(PokedexContentProvider.CONTENT_URI, values);
        Toast.makeText(this, "DONE", Toast.LENGTH_LONG).show();
    }

    private void fetchPokemonFromAPI(String nameOrId) {
        AndroidNetworking.get("https://pokeapi.co/api/v2/pokemon/{nameOrId}")
                .addPathParameter("nameOrId", nameOrId.toLowerCase())
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String name = response.getString("name");
                            int id = response.getInt("id");
                            int height = response.getInt("height");
                            int weight = response.getInt("weight");
                            String imageUrl = response.getJSONObject("sprites").getString("front_default");

                            StringBuilder typesBuilder = new StringBuilder();
                            for (int i = 0; i < response.getJSONArray("types").length(); i++) {
                                typesBuilder.append(
                                        response.getJSONArray("types")
                                                .getJSONObject(i)
                                                .getJSONObject("type")
                                                .getString("name")
                                );
                                if (i < response.getJSONArray("types").length() - 1) typesBuilder.append(", ");
                            }

                            StringBuilder statsBuilder = new StringBuilder();
                            for (int i = 0; i < response.getJSONArray("stats").length(); i++) {
                                JSONObject statObj = response.getJSONArray("stats").getJSONObject(i);
                                String statName = statObj.getJSONObject("stat").getString("name");
                                int statValue = statObj.getInt("base_stat");
                                statsBuilder.append(statName).append(": ").append(statValue);
                                if (i < response.getJSONArray("stats").length() - 1) statsBuilder.append(", ");
                            }

                            String heightWeight = height + " dm / " + weight + " hg";

                            openPokemonDisplayActivity(name, id, typesBuilder.toString(), heightWeight, statsBuilder.toString(), imageUrl);

                        } catch (JSONException e) {
                            Toast.makeText(PokedexMain.this, "Error parsing Pokémon data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(PokedexMain.this, "Error fetching Pokémon", Toast.LENGTH_SHORT).show();
                        Log.e("POKEAPI", anError.getMessage());
                    }
                });
    }

    private void openPokemonDisplayActivity(String name, int id, String types, String heightWeight, String stats, String imageUrl) {
        Intent intent = new Intent(this, PokemonDisplayActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("nationalNumber", id);
        intent.putExtra("types", types);
        intent.putExtra("heightWeight", heightWeight);
        intent.putExtra("stats", stats);
        intent.putExtra("imageUrl", imageUrl);
        startActivity(intent);
    }
}
