package com.example.pokedex;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

public class DatabaseView extends AppCompatActivity {

    ListView listView;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        EdgeToEdge.enable(this);
        setContentView(R.layout.data); // make sure data.xml contains a ListView with id "listView"

        listView = findViewById(R.id.listView);

        // Query all rows explicitly including "_id"
        Cursor cursor = getContentResolver().query(
                PokedexContentProvider.CONTENT_URI,
                new String[]{
                        "_ID", // required for SimpleCursorAdapter ????
                        PokedexContentProvider.COL_NATIONALNUMBER,
                        PokedexContentProvider.COL_NAME,

                },
                null,
                null,
                null
        );

        // Map database columns to row.xml TextViews
        String[] from = {
                PokedexContentProvider.COL_NATIONALNUMBER,
                PokedexContentProvider.COL_NAME,

        };

        int[] to = {
                R.id.tvNationalNumber,
                R.id.tvName,
                R.id.tvSpecies,
                R.id.tvHP
        };
        // erm

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.row, // your custom row layout
                cursor,
                from,
                to,
                0
        );

        listView.setAdapter(adapter);

        // optional to delete on long pressw wait
        // ayyy
        // this works
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Delete row by _id
            getContentResolver().delete(
                    PokedexContentProvider.CONTENT_URI,
                    "_ID=?",
                    new String[]{String.valueOf(id)}
            );

            // Refresh adapter cursor
            Cursor newCursor = getContentResolver().query(
                    PokedexContentProvider.CONTENT_URI,
                    new String[]{
                            "_ID",
                            PokedexContentProvider.COL_NATIONALNUMBER,
                            PokedexContentProvider.COL_NAME,

                    },
                    null, null, null
            );
            adapter.changeCursor(newCursor);
            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
    }
}
