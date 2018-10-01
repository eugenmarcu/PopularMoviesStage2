package com.example.android.popularmoviesstage2.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmoviesstage2.database.AppDatabase;
import com.example.android.popularmoviesstage2.database.MovieEntry;


public class DetailsViewModel extends ViewModel {

    private LiveData<MovieEntry> movie;

    public DetailsViewModel(AppDatabase database, String movieId) {
        movie = database.movieDao().loadMovieById(movieId);
    }

    public LiveData<MovieEntry> getMovie() {
        return movie;
    }
}
