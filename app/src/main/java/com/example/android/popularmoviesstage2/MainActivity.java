package com.example.android.popularmoviesstage2;

import android.app.LoaderManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.ViewModels.MainViewModel;
import com.example.android.popularmoviesstage2.database.MovieEntry;
import com.example.android.popularmoviesstage2.movie.MovieAdapter;
import com.example.android.popularmoviesstage2.movie.MovieLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener, LoaderManager.LoaderCallbacks {

    private static final String SECTION_POPULAR = "popular";
    private static final String SECTION_TOP_RATED = "top_rated";
    private static final String SECTION_FAVORITES = "favorites";
    private static final String MOVIE_KEY = "movie";
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
    private List<MovieEntry> mMovieList;
    private MainViewModel viewModel;

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("sort_by", mSection);
        viewModel.setSection(mSection);
    }


    @Override
    protected void onResume() {
        super.onResume();
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        if (viewModel.getSection() == SECTION_FAVORITES) {
            showFavorites(viewModel.getMovies().getValue());
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        MovieEntry currentMovie = adapter.getItem(position);
        intent.putExtra(MOVIE_KEY, currentMovie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
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
                mSection = SECTION_FAVORITES;
                setupFavorites();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFavorites(List<MovieEntry> movieEntries) {
        adapter.mMovieList.clear();
        adapter.addAll(movieEntries);
        recyclerView.setAdapter(adapter);
    }

    private void setupFavorites() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                showFavorites(movieEntries);
            }
        });
    }


    @Override
    public Loader onCreateLoader(int loaderID, Bundle bundle) {
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(REQUEST_URL + mSection);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("api_key", BuildConfig.ApiKey);

        if (mSection != SECTION_FAVORITES)
            return new MovieLoader(this, uriBuilder.toString());
        else
            return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object list) {
        emptyView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        int loaderID = loader.getId();
        if (loaderID == MOVIE_LOADER_ID && mSection != SECTION_FAVORITES) {
            List<MovieEntry> movieList = (List<MovieEntry>) list;

            adapter.mMovieList.clear();
            if (movieList != null && movieList.size() > 0) {

                adapter.addAll(movieList);
                recyclerView.setAdapter(adapter);
            } else {
                recyclerView.setAdapter(adapter);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(R.string.no_movies_found);
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

}