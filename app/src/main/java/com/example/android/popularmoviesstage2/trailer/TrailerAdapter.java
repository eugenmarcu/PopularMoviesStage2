package com.example.android.popularmoviesstage2.trailer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.android.popularmoviesstage2.R;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private List<Trailer> trailerList;
    private Context mContext;

    /**
     * ReviewAdapter constructor that will take the trailerList to display within context
     *
     * @param context     the context within will be displayed the reviewsList
     * @param trailerList the list of reviews that will be displayed
     */
    public TrailerAdapter(Context context, List<Trailer> trailerList) {
        this.mContext = context;
        this.trailerList = trailerList;
    }


    @NonNull
    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_list_item, parent, false);
        return new TrailerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.TrailerViewHolder holder, int position) {
        final Trailer currentTrailer = trailerList.get(position);

        //get the holder that should be updated for each review details
        TextView name = holder.name;
        name.setText(currentTrailer.getName());

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + currentTrailer.getSource()));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public void addAll(List<Trailer> trailerList) {
        this.trailerList.clear();
        this.trailerList.addAll(trailerList);
        notifyDataSetChanged();
    }

    public void clearAll() {
        this.trailerList.clear();
        notifyDataSetChanged();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        //ViewHolder's constructor
        public TrailerViewHolder(View itemView) {
            super(itemView);

            //Find Views that will display each item
            name = itemView.findViewById(R.id.trailers_name_tv);
        }
    }
}
