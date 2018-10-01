package com.example.android.popularmoviesstage2.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Movie;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "movies")
public class MovieEntry implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int key;
    private String id;
    private String title;
    private String releaseDate;
    private String vote;
    private String plot;
    private String poster;

    @Ignore
    public MovieEntry(String id, String title, String releaseDate, String vote, String plot, String poster){
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.vote = vote;
        this.plot = plot;
        this.poster = poster;
    }

    public MovieEntry(int key, String id, String title, String releaseDate, String vote, String plot, String poster){
        this.key = key;
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.vote = vote;
        this.plot = plot;
        this.poster = poster;
    }

    public int getKey(){return key;}

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getVote() {
        return vote;
    }

    public String getPlot() {
        return plot;
    }

    public String getPoster() {
        return poster;
    }

    public static final Creator CREATOR = new Creator() {
        public MovieEntry createFromParcel(Parcel in) {
            return new MovieEntry(in);
        }

        public MovieEntry[] newArray(int size) {
            return new MovieEntry[size];
        }
    };

    public MovieEntry(Parcel in){
        this.id = in.readString();
        this.title = in.readString();
        this.releaseDate = in.readString();
        this.vote =  in.readString();
        this.plot = in.readString();
        this.poster = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.title);
        parcel.writeString(this.releaseDate);
        parcel.writeString(this.vote);
        parcel.writeString(this.plot);
        parcel.writeString(this.poster);
    }
}
