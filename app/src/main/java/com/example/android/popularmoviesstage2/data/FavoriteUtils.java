package com.example.android.popularmoviesstage2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.popularmoviesstage2.data.FavoriteMovieContract.MovieEntry;
import com.example.android.popularmoviesstage2.movie.Movie;

public class FavoriteUtils {

    public static boolean isFavorite(Context context, String movieId) {
        String selection = MovieEntry.COLUMN_MOVIE_ID + " = ?";
        String[] selectionArgs = {""};
        selectionArgs[0] = movieId;
        Cursor cursor = context.getContentResolver().query(MovieEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null);
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static int removeFavorite(Context context, String movieId) {
        Uri uri = MovieEntry.CONTENT_URI;
        Uri removeUri = uri.buildUpon().appendPath(movieId).build();
        return context.getContentResolver().delete(removeUri, null, null);
    }

    public static Uri addFavorite(Context context, Movie favoriteMovie) {

        ContentValues cv = new ContentValues();
        cv.put(MovieEntry.COLUMN_MOVIE_POSTER, favoriteMovie.getPosterUrl());
        cv.put(MovieEntry.COLUMN_MOVIE_PLOT, favoriteMovie.getPlot());
        cv.put(MovieEntry.COLUMN_MOVIE_VOTE, favoriteMovie.getVote());
        cv.put(MovieEntry.COLUMN_MOVIE_DATE, favoriteMovie.getReleaseDate());
        cv.put(MovieEntry.COLUMN_MOVIE_TITLE, favoriteMovie.getTitle());
        cv.put(MovieEntry.COLUMN_MOVIE_ID, favoriteMovie.getId());
        return context.getContentResolver().insert(MovieEntry.CONTENT_URI, cv);
    }
}
