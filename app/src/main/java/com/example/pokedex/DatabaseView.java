package com.example.pokedex;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

public class DatabaseView extends AppCompatActivity {

    ListView listView;
    SimpleCursorAdapter adapter;
    Button backBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.data);

        listView = findViewById(R.id.listView);
        backBut = findViewById(R.id.backButton);

        Cursor cursor = getContentResolver().query(
                PokedexContentProvider.CONTENT_URI,
                new String[]{"_ID AS _id", PokedexContentProvider.COL_NATIONALNUMBER, PokedexContentProvider.COL_NAME},
                null, null, null
        );


        String[] from = {PokedexContentProvider.COL_NATIONALNUMBER, PokedexContentProvider.COL_NAME};
        int[] to = {R.id.tvNationalNumber, R.id.tvName};

        adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, from, to, 0);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor selected = (Cursor) adapter.getItem(position);
            String name = selected.getString(selected.getColumnIndexOrThrow(PokedexContentProvider.COL_NAME));
            String number = selected.getString(selected.getColumnIndexOrThrow(PokedexContentProvider.COL_NATIONALNUMBER));
            Intent intent = new Intent(this, PokemonDisplayActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("nationalNumber", Integer.parseInt(number));
            startActivity(intent);
        });

        backBut.setOnClickListener(v -> {
            finish(); // This closes the current activity and goes back
        });
    } // end on create

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null && adapter.getCursor() != null) adapter.getCursor().close();
    }
}
