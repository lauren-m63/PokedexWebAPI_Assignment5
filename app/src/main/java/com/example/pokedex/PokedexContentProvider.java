package com.example.pokedex;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class PokedexContentProvider extends ContentProvider {

    public static final String TABLE_NAME= "PokedexTable";
    public static final String COL_NATIONALNUMBER= "NationalNumber";
    public static final String COL_NAME= "Name";

    public static final String DB_NAME= "PokedexDB";
    MainDatabaseHelper mHelper;

    public final static String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                    "_ID INTEGER PRIMARY KEY, " +
                    COL_NATIONALNUMBER + " INTEGER, " +
                    COL_NAME + " TEXT) ";


    public static final Uri CONTENT_URI = Uri.parse("content://com.example.pokedex.provider");



    protected final class MainDatabaseHelper extends SQLiteOpenHelper {
        public MainDatabaseHelper(Context context) {
            super(context, DB_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE);
        } //end onCreate

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    } // last MainDatabaseHelper


    public PokedexContentProvider() {
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mHelper.getWritableDatabase().delete(TABLE_NAME, selection, selectionArgs);
    }


    @Override
    public String getType(Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int nationalNumber = values.getAsInteger(COL_NATIONALNUMBER);
        String name = values.getAsString(COL_NAME);

        long id = mHelper.getWritableDatabase().insert(TABLE_NAME, null, values);

        return Uri.withAppendedPath(uri, String.valueOf(id));
        //not everything stored as string to convert it to append with id

    }

    @Override
    public boolean onCreate() {
        mHelper= new MainDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        if (projection == null) {
            projection = new String[]{
                    "_ID as _id",  // alias _ID to _id for adapter
                    COL_NATIONALNUMBER,
                    COL_NAME,
            };
        } else {
            // Alias _ID if included
            for (int i = 0; i < projection.length; i++) {
                if (projection[i].equals("_ID")) {
                    projection[i] = "_ID as _id";
                }
            }
        }
        Cursor c = mHelper.getReadableDatabase().query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}