package com.example.android.popularmoviesstage2.movie;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popularmoviesstage2.QueryUtils;
import com.example.android.popularmoviesstage2.database.MovieEntry;

import java.util.List;

/**
 * Loads a list of movies by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class MovieLoader extends AsyncTaskLoader<List<MovieEntry>> {
    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = MovieLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link MovieLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<MovieEntry> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of movies.
        List<MovieEntry> movieEntryList = QueryUtils.fetchMovieData(mUrl);
        return movieEntryList;
    }

}
