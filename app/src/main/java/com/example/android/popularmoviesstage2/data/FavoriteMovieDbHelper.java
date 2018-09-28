package com.example.android.popularmoviesstage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteMovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Favorite_movies.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create-Table
        String SQL_CREATE_BOOK_TABLE = "CREATE TABLE " + FavoriteMovieContract.MovieEntry.TABLE_NAME + " ("
                + FavoriteMovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FavoriteMovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + FavoriteMovieContract.MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, "
                + FavoriteMovieContract.MovieEntry.COLUMN_MOVIE_PLOT + " TEXT NOT NULL, "
                + FavoriteMovieContract.MovieEntry.COLUMN_MOVIE_VOTE + " TEXT NOT NULL, "
                + FavoriteMovieContract.MovieEntry.COLUMN_MOVIE_DATE + " TEXT NOT NULL, "
                + FavoriteMovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_BOOK_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieContract.MovieEntry.TABLE_NAME);
            onCreate(db);
        }
    }
}