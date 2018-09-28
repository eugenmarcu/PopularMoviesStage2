package com.example.android.popularmoviesstage2.review;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.R;

import java.util.List;
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviewsList;
    private Context mContext;

    /**
     * ReviewAdapter constructor that will take the reviewList to display within context
     *
     * @param context     the context within will be displayed the reviewsList
     * @param reviewsList the list of reviews that will be displayed
     */
    public ReviewAdapter(Context context, List<Review> reviewsList) {
        this.mContext = context;
        this.reviewsList = reviewsList;
    }


    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {
        Review currentReview = reviewsList.get(position);

        //get the holder that should be updated for each review details
        TextView author = holder.author;
        author.setText(currentReview.getAuthor());
        final TextView content = holder.content;
        content.setText(currentReview.getContent());

        final int maxLines = 5;
        content.setMaxLines(maxLines);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (content.getMaxLines() == maxLines) {
                    content.setMaxLines(content.length());
                } else {
                    content.setMaxLines(maxLines);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public void addAll(List<Review> reviewsList) {
        this.reviewsList.clear();
        this.reviewsList.addAll(reviewsList);
        notifyDataSetChanged();
    }

    public void clearAll() {
        this.reviewsList.clear();
        notifyDataSetChanged();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        public TextView author;
        public TextView content;

        //ViewHolder's constructor
        public ReviewViewHolder(View itemView) {
            super(itemView);

            //Find Views that will display each item
            author = itemView.findViewById(R.id.reviews_author_tv);
            content = itemView.findViewById(R.id.reviews_content_tv);
        }
    }
}
