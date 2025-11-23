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

// import com.androidnetworking.AndroidNetworking;
// import com.androidnetworking.common.Priority;
// import com.androidnetworking.error.ANError;
// import com.androidnetworking.interfaces.JSONObjectRequestListener;

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

    // Checks if all the fields are filled
    boolean allFieldsFilled(EditText... fields) {
        for (EditText field : fields) {
            if (field.getText().toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.table);

        // Initialize Networking library (commented out, original version)
        // AndroidNetworking.initialize(getApplicationContext());

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
        dataButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DatabaseView.class);
            startActivity(intent);
        });

        // Fetch Pokémon from API when clicking display button
        pokemonDisplayButton.setOnClickListener(v -> {
            String nameOrId = nameInput.getText().toString().trim();
            if (!nameOrId.isEmpty()) {
                fetchPokemonFromAPI(nameOrId);
            } else {
                Toast.makeText(this, "Please enter a Pokémon name or number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSubmit() {
        if (!allFieldsFilled(nationalNumberInput, nameInput)) {
            Toast.makeText(this, "Please fill out both fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int number = Integer.parseInt(nationalNumberInput.getText().toString());
        String name = nameInput.getText().toString();

        StringBuilder fixIt = new StringBuilder();
        if (!pokedex.setNumber(number)) fixIt.append("National Number, ");
        if (!pokedex.setName(name)) fixIt.append("Name, ");

        if (fixIt.length() > 0) {
            fixIt.setLength(fixIt.length() - 2);
            Toast.makeText(this, "The following fields are not within bounds: " + fixIt, Toast.LENGTH_LONG).show();
            return;
        }

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

        ContentValues values = new ContentValues();
        values.put(PokedexContentProvider.COL_NATIONALNUMBER, number);
        values.put(PokedexContentProvider.COL_NAME, name);
        getContentResolver().insert(PokedexContentProvider.CONTENT_URI, values);

        Toast.makeText(this, "DONE", Toast.LENGTH_LONG).show();
    }

    private void fetchPokemonFromAPI(String nameOrId) {
        // Original version: networking removed
        // AndroidNetworking.get("https://pokeapi.co/api/v2/pokemon/{nameOrId}")
        //         .addPathParameter("nameOrId", nameOrId.toLowerCase())
        //         .setPriority(Priority.LOW)
        //         .build()
        //         .getAsJSONObject(new JSONObjectRequestListener() {
        //             @Override
        //             public void onResponse(JSONObject response) {
        //                 try {
        //                     String name = response.getString("name");
        //                     int id = response.getInt("id");
        //                     int height = response.getInt("height");
        //                     int weight = response.getInt("weight");
        //
        //                     openPokemonDisplayActivity(name, id, height, weight);
        //
        //                 } catch (JSONException e) {
        //                     e.printStackTrace();
        //                     Toast.makeText(PokedexMain.this, "Error parsing Pokémon data", Toast.LENGTH_SHORT).show();
        //                 }
        //             }
        //
        //             @Override
        //             public void onError(ANError anError) {
        //                 Toast.makeText(PokedexMain.this, "Error fetching Pokémon", Toast.LENGTH_SHORT).show();
        //                 Log.e("POKEAPI", anError.getMessage());
        //             }
        //         });
    }

    private void openPokemonDisplayActivity(String name, int id, int height, int weight) {
        Intent intent = new Intent(this, PokemonDisplayActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("id", id);
        intent.putExtra("height", height);
        intent.putExtra("weight", weight);
        startActivity(intent);
    }
}
