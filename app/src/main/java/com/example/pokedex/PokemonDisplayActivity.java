package com.example.pokedex;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import androidx.activity.EdgeToEdge;

public class PokemonDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pokemon_display);

        ImageView sprite = findViewById(R.id.pokemonSprite);
        TextView name = findViewById(R.id.pokemonName);
        TextView number = findViewById(R.id.pokemonNumber);
        TextView types = findViewById(R.id.pokemonTypes);
        TextView heightWeight = findViewById(R.id.pokemonHeightWeight);
        TextView stats = findViewById(R.id.pokemonStats);
        Button backButton = findViewById(R.id.backButton);

        String imageUrl = getIntent().getStringExtra("imageUrl");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(sprite);
        }

        name.setText(getIntent().getStringExtra("name"));
        number.setText("National Number: " + getIntent().getIntExtra("nationalNumber", 0));
        types.setText("Type(s): " + getIntent().getStringExtra("types"));
        heightWeight.setText("Height / Weight: " + getIntent().getStringExtra("heightWeight"));
        stats.setText("Stats: " + getIntent().getStringExtra("stats"));

        backButton.setOnClickListener(v -> finish());
    }
}
