package com.example.android.popularmoviesstage2.trailer;

public class Trailer {

    private String name;
    private String source;

    public Trailer(String name, String source){
        this.name = name;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }
}
