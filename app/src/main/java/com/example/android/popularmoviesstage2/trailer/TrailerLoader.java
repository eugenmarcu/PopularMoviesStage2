package com.example.android.popularmoviesstage2.trailer;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popularmoviesstage2.QueryUtils;

import java.util.List;

/**
 * Loads a list of trailers by using an AsyncTask to perform the
 * network request to the given URL.
 */

public class TrailerLoader extends AsyncTaskLoader<List<Trailer>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = com.example.android.popularmoviesstage2.trailer.TrailerLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link com.example.android.popularmoviesstage2.trailer.TrailerLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public TrailerLoader(Context context, String url) {
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
    public List<Trailer> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of reviews.
        List<Trailer> trailers = QueryUtils.fetchTrailerData(mUrl);
        return trailers;
    }

}
