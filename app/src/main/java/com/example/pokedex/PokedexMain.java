package com.example.pokedex;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class PokedexMain extends AppCompatActivity {

    // editing from old Pokedex to make less manual input

    EditText nationalNumberInput;
    EditText nameInput;

    Button submitButton;
    Button resetButton;
    Button dataButton;
    Pokedex pokedex;

    // checks if all fields are filled
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
        setContentView(R.layout.table); // Make sure res/layout/table.xml exists
        pokedex = new Pokedex(this);

        // initialize input fields and buttons
        nationalNumberInput = findViewById(R.id.nationalNumberInput);
        nameInput = findViewById(R.id.nameInput);
        submitButton = findViewById(R.id.submitButton);
        resetButton = findViewById(R.id.resetButton);
        dataButton = findViewById(R.id.numberButton);

        // handle submit button click
        submitButton.setOnClickListener(v -> {

            // checking if fields are empty
            if (!allFieldsFilled(nationalNumberInput, nameInput)) {
                Toast.makeText(this, "Please fill out both fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            int number = Integer.parseInt(nationalNumberInput.getText().toString());
            String name = nameInput.getText().toString();

            // validate inputs
            StringBuilder fixIt = new StringBuilder();
            if (!pokedex.setNumber(number)) fixIt.append("National Number, ");
            if (!pokedex.setName(name)) fixIt.append("Name, ");

            // show errors if any
            if (fixIt.length() > 0) {
                fixIt.setLength(fixIt.length() - 2); // remove last comma and space
                Toast.makeText(this, "The following fields are not within bounds: " + fixIt, Toast.LENGTH_LONG).show();
                return;
            }

            // check duplicates in database
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
                return; // stop insert
            }
            if (cursor != null) cursor.close();

            // insert new Pokémon into database
            ContentValues values = new ContentValues();
            values.put(PokedexContentProvider.COL_NATIONALNUMBER, number);
            values.put(PokedexContentProvider.COL_NAME, name);
            getContentResolver().insert(PokedexContentProvider.CONTENT_URI, values);

            // log all entries in database
            Cursor c = getContentResolver().query(PokedexContentProvider.CONTENT_URI, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                do {
                    String message = "";
                    for (int i = 0; i < c.getColumnCount(); i++) {
                        message += c.getString(i) + " ";
                    }
                    Log.i("LAUREN", message.trim());
                } while (c.moveToNext());
                c.close();
            }

            Toast.makeText(this, "DONE", Toast.LENGTH_LONG).show();
        });

        // handle reset button click
        resetButton.setOnClickListener(v -> {
            nationalNumberInput.setText("896");
            nameInput.setText("Glastrier");
        });

        // handle data button click to view database
        dataButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DatabaseView.class);
            startActivity(intent);
        });
    }
}
