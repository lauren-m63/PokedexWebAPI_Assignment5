package com.example.pokedex;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PokemonDisplayActivity extends AppCompatActivity {

    // Views from the XML
    private ImageView pokemonSprite;
    private TextView pokemonName;
    private TextView pokemonNumber;
    private TextView pokemonTypes;
    private TextView pokemonHeightWeight;
    private TextView pokemonStats;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pokemon_display); // your XML page

        // Bind the views
        pokemonSprite = findViewById(R.id.pokemonSprite);
        pokemonName = findViewById(R.id.pokemonName);
        pokemonNumber = findViewById(R.id.pokemonNumber);
        pokemonTypes = findViewById(R.id.pokemonTypes);
        pokemonHeightWeight = findViewById(R.id.pokemonHeightWeight);
        pokemonStats = findViewById(R.id.pokemonStats);
        backButton = findViewById(R.id.backButton);

        // Back button to finish activity and return
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Example: Get Pokémon data from Intent (sent from previous activity)
        int nationalNumber = getIntent().getIntExtra("nationalNumber", 0);
        String name = getIntent().getStringExtra("name");
        String types = getIntent().getStringExtra("types"); // comma-separated
        String heightWeight = getIntent().getStringExtra("heightWeight"); // e.g., "2.0 m / 60 kg"
        String stats = getIntent().getStringExtra("stats"); // e.g., "HP: 45, Attack: 49, Defense: 49"

        // Set data to views
        pokemonName.setText(name != null ? name : "Unknown");
        pokemonNumber.setText("National Number: " + nationalNumber);
        pokemonTypes.setText("Type(s): " + (types != null ? types : "Unknown"));
        pokemonHeightWeight.setText("Height / Weight: " + (heightWeight != null ? heightWeight : "Unknown"));
        pokemonStats.setText("Stats: " + (stats != null ? stats : "Unknown"));


        // Glide.with(this).load(spriteUrl).into(pokemonSprite);
    }
}
