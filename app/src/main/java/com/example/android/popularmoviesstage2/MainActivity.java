package com.example.android.popularmoviesstage2;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.data.FavoriteMovieContract.MovieEntry;
import com.example.android.popularmoviesstage2.movie.Movie;
import com.example.android.popularmoviesstage2.movie.MovieAdapter;
import com.example.android.popularmoviesstage2.movie.MovieLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener, LoaderManager.LoaderCallbacks {

    private static final String SECTION_POPULAR = "popular";
    private static final String SECTION_TOP_RATED = "top_rated";
    public final String REQUEST_URL = "http://api.themoviedb.org/3/movie/";

    @BindView(R.id.rv_list)
    RecyclerView recyclerView;
    MovieAdapter adapter;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private String mSection = SECTION_POPULAR;
    private LoaderManager mLoaderManager;
    private static final int MOVIE_LOADER_ID = 101;
    private static final int DATABASE_LOADER_ID = 102;
    private List<Movie> mMovieList;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        int numberOfColumns = 2;
        mMovieList = new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new MovieAdapter(this, mMovieList);
        adapter.setClickListener(MainActivity.this);
        recyclerView.setAdapter(adapter);
        // Get a reference to the LoaderManager, in order to interact with loaders.
        mLoaderManager = getLoaderManager();

        if (!isInternetConnection()) {
            adapter.mMovieList.clear();
            emptyView.setText(R.string.no_internet);
            progressBar.setVisibility(View.GONE);
        } else {
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            mLoaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        Movie currentMovie = adapter.getItem(position);
        intent.putExtra("movie", currentMovie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_order_by_most_popular:
                mSection = SECTION_POPULAR;
                mLoaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
                return true;

            case R.id.action_order_by_higher_rated:
                mSection = SECTION_TOP_RATED;
                mLoaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
                return true;

            case R.id.action_order_by_favorites:
                if (mLoaderManager.getLoader(DATABASE_LOADER_ID) == null)
                    mLoaderManager.restartLoader(DATABASE_LOADER_ID, null, this);
                else
                    mLoaderManager.initLoader(DATABASE_LOADER_ID, null, this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader onCreateLoader(int loaderID, Bundle bundle) {
        if (loaderID == MOVIE_LOADER_ID) {
            // parse breaks apart the URI string that's passed into its parameter
            Uri baseUri = Uri.parse(REQUEST_URL + mSection);

            // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
            Uri.Builder uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("api_key", BuildConfig.ApiKey);

            return new MovieLoader(this, uriBuilder.toString());
        } else {
            String[] projection = {
                    MovieEntry._ID,
                    MovieEntry.COLUMN_MOVIE_ID,
                    MovieEntry.COLUMN_MOVIE_POSTER,
                    MovieEntry.COLUMN_MOVIE_PLOT,
                    MovieEntry.COLUMN_MOVIE_DATE,
                    MovieEntry.COLUMN_MOVIE_VOTE,
                    MovieEntry.COLUMN_MOVIE_TITLE};

            return new CursorLoader(this,
                    MovieEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object list) {
        emptyView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        int loaderID = loader.getId();
        if (loaderID == MOVIE_LOADER_ID) {
            List<Movie> movieList = (List<Movie>) list;

            adapter.mMovieList.clear();
            if (movieList != null && movieList.size() > 0) {

                adapter.addAll(movieList);
                recyclerView.setAdapter(adapter);
            } else {
                recyclerView.setAdapter(adapter);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(R.string.no_movies_found);
            }
        } else {
            //Database Loader
            Cursor cursor = (Cursor) list;
            List<Movie> movieList = new ArrayList<>();
            if(cursor.moveToFirst()){
                movieList.add(getMovieFromCursor(cursor));
            }

            while (cursor.moveToNext()) {
                movieList.add(getMovieFromCursor(cursor));
            }
            adapter.mMovieList.clear();
            adapter.addAll(movieList);
            recyclerView.setAdapter(adapter);
            if(movieList.size() == 0){
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(R.string.no_favorites_found);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.mMovieList.clear();
    }

    private boolean isInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private Movie getMovieFromCursor(Cursor cursor){
        int idColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID);
        int posterColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER);
        int plotColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_PLOT);
        int dateColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_DATE);
        int voteColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_VOTE);
        int titleColumnIndex = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE);

        String id = cursor.getString(idColumnIndex);
        String poster = cursor.getString(posterColumnIndex);
        String plot = cursor.getString(plotColumnIndex);
        String date = cursor.getString(dateColumnIndex);
        String vote = cursor.getString(voteColumnIndex);
        String title = cursor.getString(titleColumnIndex);

        return new Movie(id, title, date, vote, plot, poster);
    }
}