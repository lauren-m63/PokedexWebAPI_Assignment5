package com.example.pokedex;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;

public class PokedexMain extends AppCompatActivity {

    // editing from old Pokedex to make less manual input

    EditText nationalNumberInput;
    EditText nameInput;
    EditText speciesInput;
   int genderInput;
    EditText heightInput;
    EditText weightInput;
    Spinner levelSpinner;
    EditText HPInput;
    EditText attackInput;
    EditText defenseInput;
    Button submitButton;
    Button resetButton;
    Pokedex pokedex;
    Button dataButton;

    public boolean emptyString(String string){
        if (!string.isEmpty()){
            return true;
        }
        return false;
    }

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
        setContentView(R.layout.table); // Make sure res/layout/layout.xml exists
        pokedex= new Pokedex(this);

        nationalNumberInput = findViewById(R.id.nationalNumberInput);
        nameInput = findViewById(R.id.nameInput);
        speciesInput = findViewById(R.id.speciesInput);
        heightInput = findViewById(R.id.heightInput);
        weightInput = findViewById(R.id.weightInput);
        HPInput = findViewById(R.id.HPInput);
        attackInput = findViewById(R.id.attackInput);
        defenseInput = findViewById(R.id.defenseInput);
        levelSpinner = findViewById(R.id.levelSpinner);
       //genderInput = findViewById(genderInput);
        submitButton  = findViewById(R.id.submitButton);
        resetButton = findViewById(R.id.resetButton);
        dataButton = findViewById(R.id.numberButton);


        LinkedList<String> levels = new LinkedList<>();
        for (int i = 1; i <= 50; i++) {
            levels.add(String.valueOf(i));
        }
        ArrayAdapter<String> levelsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, levels);
        levelsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(levelsAdapter);



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //checking if fields are empty
                if (!allFieldsFilled(
                        nationalNumberInput,
                        nameInput,
                        speciesInput,
                        heightInput,
                        weightInput,
                        HPInput,
                        attackInput,
                        defenseInput
                )) {
                    Toast.makeText(v.getContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (allFieldsFilled()){
                    String nationalNumberInputString = nationalNumberInput.getText().toString();
                        int nationalNumberInputInt= Integer.parseInt(nationalNumberInputString);
                    String nameInputString = nameInput.getText().toString();
                    String speciesInputString = speciesInput.getText().toString();
                    String heightInputString = heightInput.getText().toString();
                        double heightInputDouble = Double.parseDouble(heightInputString);
                    String weightInputString = weightInput.getText().toString();
                        double weightInputDouble = Double.parseDouble(weightInputString);

                    String selectedLevelString = levelSpinner.getSelectedItem().toString();
                        int selectedLevel = Integer.parseInt(selectedLevelString);
                    String hpStringInput = HPInput.getText().toString();
                        int hpInputInt= Integer.parseInt(hpStringInput);
                    String attackInputString = attackInput.getText().toString();
                        int attackInputInt= Integer.parseInt(attackInputString);
                    String defenseInputString = defenseInput.getText().toString();
                        int defenseInputInt= Integer.parseInt(defenseInputString);

                    StringBuilder fixIt = new StringBuilder();

                    if (!pokedex.setNumber(nationalNumberInputInt)) {
                        fixIt.append("National Number, ");
                        nationalNumberInput.setTextColor(getResources().getColor(R.color.red));
                    }
                    if (!pokedex.setName(nameInputString)) {
                        fixIt.append("Name, ");
                        nameInput.setTextColor(getResources().getColor(R.color.red));
                    }
                    if (!pokedex.setSpecies(speciesInputString)) {
                        fixIt.append("Species, ");
                        speciesInput.setTextColor(getResources().getColor(R.color.red));
                    }
                    if (!pokedex.setHeight(heightInputDouble)) {
                        fixIt.append("Height, ");
                        heightInput.setTextColor(getResources().getColor(R.color.red));
                    }
                    if (!pokedex.setWeight(weightInputDouble)) {
                        fixIt.append("Weight, ");
                        weightInput.setTextColor(getResources().getColor(R.color.red));
                    }
                    if (!pokedex.setLevel(selectedLevel)) {
                        fixIt.append("Level, ");
                        // Spinner label would need to be handled separately
                    }
                    if (!pokedex.setHp(hpInputInt)) {
                        fixIt.append("HP, ");
                        HPInput.setTextColor(getResources().getColor(R.color.red));
                    }
                    if (!pokedex.setAttack(attackInputInt)) {
                        fixIt.append("Attack, ");
                        attackInput.setTextColor(getResources().getColor(R.color.red));
                    }
                    if (!pokedex.setDefense(defenseInputInt)) {
                        fixIt.append("Defense, ");
                        defenseInput.setTextColor(getResources().getColor(R.color.red));
                    }



                    if (fixIt.length() > 0) {
                        fixIt.setLength(fixIt.length() - 2); // remove last comma and space
                        Toast.makeText(v.getContext(), "The following fields are not within bounds: " + fixIt.toString(), Toast.LENGTH_LONG).show();
                    // toast and then im leaving it red but it also says what is wrong with it so its user friendl
                        // in ONE toast and not a million - user friendly yo

                    } else {
//                        heightInput.setText(heightInputString + " m");
//                        weightInput.setText(heightInputString + " Kg"); bug when doing data
                        Toast.makeText(v.getContext(), "Data passes inspection", Toast.LENGTH_LONG).show();

                        //adding to database upon submit


                        //duplicates
                        Cursor checkCursor = getContentResolver().query(
                                PokedexContentProvider.CONTENT_URI,
                                null,
                                PokedexContentProvider.COL_NATIONALNUMBER + "=?",
                                new String[]{ String.valueOf(nationalNumberInputInt) },
                                null
                        );

                        if (checkCursor != null && checkCursor.getCount() > 0) {
                            Toast.makeText(v.getContext(), "A Pokémon with this National Number already exists.", Toast.LENGTH_LONG).show();
                            checkCursor.close();
                            return; // stop insert
                        }
                        if (checkCursor != null) checkCursor.close();

// Insert new Pokémon
                        ContentValues values = new ContentValues();
                        values.put(PokedexContentProvider.COL_NATIONALNUMBER, nationalNumberInputInt);
                        values.put(PokedexContentProvider.COL_NAME, nameInputString);
                        values.put(PokedexContentProvider.COL_SPECIES, speciesInputString);
                        values.put(PokedexContentProvider.COL_HEIGHT, heightInputDouble);
                        values.put(PokedexContentProvider.COL_WEIGHT, weightInputDouble);
                        values.put(PokedexContentProvider.COL_LEVEL, selectedLevel);
                        values.put(PokedexContentProvider.COL_HP, hpInputInt);
                        values.put(PokedexContentProvider.COL_ATTACK, attackInputInt);
                        values.put(PokedexContentProvider.COL_DEFENSE, defenseInputInt);
                        values.put(PokedexContentProvider.COL_GENDER, "Unknown");

                        getContentResolver().insert(PokedexContentProvider.CONTENT_URI, values);


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

                        Toast.makeText(v.getContext(), "DONE", Toast.LENGTH_LONG).show();

                    }
                }// end if statement



            }
        }); // end submit on Click listener





          // Retrieve its text (e.g., "Male", "Female", "Unknown")
          //String gender = selectedRadio.getText().toString();

         // Pokedex entry = new Pokedex(
                  //number, name, species, gender,
                 // height, weight, 1, hp, attack, defense
        //  );


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nationalNumberInput.setText("896");
                nameInput.setText("Glastrier");
                speciesInput.setText("Wild Horse Pokemon");
                // gender input
                heightInput.setText("2.2");
                weightInput.setText("800.0");
                //level input
                HPInput.setText("0");
                attackInput.setText("0");
                defenseInput.setText("0");

            }
        });  // end on click listener

        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent = new Intent(v.getContext(), DatabaseView.class);
             startActivity(intent);

            }
        });  // end on click listener





    } // end on create




} // end class
