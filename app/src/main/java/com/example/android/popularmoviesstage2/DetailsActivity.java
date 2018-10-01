package com.example.android.popularmoviesstage2;

import android.app.LoaderManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Loader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.popularmoviesstage2.ViewModels.DetailsViewModel;
import com.example.android.popularmoviesstage2.ViewModels.DetailsViewModelFactory;
import com.example.android.popularmoviesstage2.database.AppDatabase;
import com.example.android.popularmoviesstage2.database.MovieEntry;
import com.example.android.popularmoviesstage2.review.Review;
import com.example.android.popularmoviesstage2.review.ReviewAdapter;
import com.example.android.popularmoviesstage2.review.ReviewLoader;
import com.example.android.popularmoviesstage2.trailer.Trailer;
import com.example.android.popularmoviesstage2.trailer.TrailerAdapter;
import com.example.android.popularmoviesstage2.trailer.TrailerLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    @BindView(R.id.tv_title)
    TextView titleTV;
    @BindView(R.id.tv_release_date)
    TextView releaseDateTV;
    @BindView(R.id.tv_average_vote)
    TextView averageVoteTV;
    @BindView(R.id.tv_plot)
    TextView plotTV;
    @BindView(R.id.iv_poster)
    ImageView posterIV;
    @BindView(R.id.btn_mark_as_favorite)
    ImageButton favoriteBtn;
    @BindView(R.id.reviews_empty_tv)
    TextView reviews_empty_tv;
    @BindView(R.id.trailer_empty_tv)
    TextView trailer_empty_tv;

    private MovieEntry currentMovie;
    private final String API_KEY = BuildConfig.ApiKey;
    private final String BASE_REQUEST_URL = "http://api.themoviedb.org/3/movie/";
    private final String REVIEWS_DIR = "/reviews?api_key=" + API_KEY;
    private final String TRAILERS_DIR = "/trailers?api_key=" + API_KEY;
    private LoaderManager mLoaderManager;
    private static final int REVIEW_LOADER_ID = 201;
    private static final int TRAILER_LOADER_ID = 202;
    private ReviewAdapter mReviewsAdapter;
    private TrailerAdapter mTrailersAdapter;
    private RecyclerView reviewsRecyclerView;
    private RecyclerView trailersRecyclerView;
    private boolean mIsFavorite;
    private AppDatabase mDb;
    private static final String MOVIE_KEY = "movie";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentMovie = bundle.getParcelable(MOVIE_KEY);
        }

        mDb = AppDatabase.getInstance(getApplicationContext());

        titleTV.setText(currentMovie.getTitle());
        releaseDateTV.setText(currentMovie.getReleaseDate());
        averageVoteTV.setText(currentMovie.getVote() + "/10");
        plotTV.setText(currentMovie.getPlot());
        Glide.with(this).load(currentMovie.getPoster()).into(posterIV);

        reviewsRecyclerView = findViewById(R.id.reviews_list);
        RecyclerView.LayoutManager layoutManagerReviews = new LinearLayoutManager(reviewsRecyclerView.getContext());
        reviewsRecyclerView.setLayoutManager(layoutManagerReviews);
        mReviewsAdapter = new ReviewAdapter(this, new ArrayList<Review>());
        reviewsRecyclerView.setAdapter(mReviewsAdapter);

        trailersRecyclerView = findViewById(R.id.trailer_list);
        RecyclerView.LayoutManager layoutManagerTrailers = new LinearLayoutManager(trailersRecyclerView.getContext());
        trailersRecyclerView.setLayoutManager(layoutManagerTrailers);
        mTrailersAdapter = new TrailerAdapter(this, new ArrayList<Trailer>());
        trailersRecyclerView.setAdapter(mTrailersAdapter);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        mLoaderManager = getLoaderManager();
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        mLoaderManager.initLoader(REVIEW_LOADER_ID, null, this);
        mLoaderManager.initLoader(TRAILER_LOADER_ID, null, this);

        DetailsViewModelFactory factory = new DetailsViewModelFactory(mDb, currentMovie.getId());
        final DetailsViewModel viewModel = ViewModelProviders.of(this, factory).get(DetailsViewModel.class);
        viewModel.getMovie().observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry movieEntry) {
                viewModel.getMovie().removeObserver(this);
                if (movieEntry == null) {
                    setFavoriteButton(false);
                } else {
                    setFavoriteButton(true);
                }

            }
        });


        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsFavorite) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDao().deleteMovie(currentMovie.getId());
                        }
                    });
                    Toast.makeText(DetailsActivity.this,"Removed from favorites",Toast.LENGTH_SHORT).show();
                    Drawable drawable = getDrawable(R.drawable.ic_favorite_border_black_24dp);
                    favoriteBtn.setImageDrawable(drawable);
                } else {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDao().insertMovie(getMovieEntryFromCurrent());
                        }
                    });
                    Toast.makeText(DetailsActivity.this,"Added to favorites",Toast.LENGTH_SHORT).show();
                    Drawable drawable = getDrawable(R.drawable.ic_favorite_black_24dp);
                    favoriteBtn.setImageDrawable(drawable);
                }
            }
        });
    }

    private MovieEntry getMovieEntryFromCurrent() {
        return new MovieEntry(currentMovie.getId(), currentMovie.getTitle(), currentMovie.getReleaseDate(), currentMovie.getVote(), currentMovie.getPlot(), currentMovie.getPoster());
    }

        private void setFavoriteButton(Boolean isFavorite) {
        mIsFavorite = isFavorite;
        if (isFavorite) {
            Drawable drawable = getDrawable(R.drawable.ic_favorite_black_24dp);
            favoriteBtn.setImageDrawable(drawable);
        } else {
            Drawable drawable = getDrawable(R.drawable.ic_favorite_border_black_24dp);
            favoriteBtn.setImageDrawable(drawable);

        }
    }

    /**
     * Return to the previous activity state on Up pressed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int loaderID, Bundle bundle) {
        if (loaderID == REVIEW_LOADER_ID) {
            // parse breaks apart the URI string that's passed into its parameter
            Uri baseUri = Uri.parse(BASE_REQUEST_URL + currentMovie.getId() + REVIEWS_DIR);
            Uri.Builder uriBuilder = baseUri.buildUpon();

            return new ReviewLoader(this, uriBuilder.toString());
        } else {
            // parse breaks apart the URI string that's passed into its parameter
            Uri baseUri = Uri.parse(BASE_REQUEST_URL + currentMovie.getId() + TRAILERS_DIR);
            Uri.Builder uriBuilder = baseUri.buildUpon();

            return new TrailerLoader(this, uriBuilder.toString());
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object list) {
        int loaderID = loader.getId();
        if (loaderID == REVIEW_LOADER_ID) {
            mReviewsAdapter.clearAll();
            List<Review> reviewList = (List<Review>) list;
            if (reviewList != null && reviewList.size() > 0) {
                reviews_empty_tv.setVisibility(View.GONE);
                mReviewsAdapter.addAll(reviewList);
            } else {
                reviews_empty_tv.setVisibility(View.VISIBLE);
            }
        } else {
            List<Trailer> trailerList = (List<Trailer>) list;
            mTrailersAdapter.clearAll();
            if (trailerList != null && trailerList.size() > 0) {
                trailer_empty_tv.setVisibility(View.GONE);
                mTrailersAdapter.addAll(trailerList);
            } else {
                trailer_empty_tv.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        int loaderID = loader.getId();
        if (loaderID == REVIEW_LOADER_ID)
            mReviewsAdapter.clearAll();
        else
            mTrailersAdapter.clearAll();
    }

}
