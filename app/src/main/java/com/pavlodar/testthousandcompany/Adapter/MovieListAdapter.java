package com.pavlodar.testthousandcompany.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pavlodar.testthousandcompany.Model.MovieList;
import com.pavlodar.testthousandcompany.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MyViewHolder> {

    Context context;
    List<MovieList> movieList;


    public MovieListAdapter(Context context, List<MovieList> movieList) {
        this.context = context;
        this.movieList = movieList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_movie_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_vote_avarage.setText(String.valueOf(movieList.get(position).getVoteAverage()));
        holder.txt_favorite.setText(movieList.get(position).getTitle());

                Picasso.get().load("https://image.tmdb.org/t/p/w500/" + movieList.get(position).getPosterPath())
                .into(holder.img_poster);


//        Picasso.get().load("https://image.tmdb.org/t/p/w500/rzRwTcFvttcN1ZpX2xv4j3tSdJu.jpg")
//                .into(holder.img_poster);

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.txt_favorite)
        TextView txt_favorite;
        @BindView(R.id.txt_vote_avarage)
        TextView txt_vote_avarage;
        @BindView(R.id.img_poster)
        ImageView img_poster;


        Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this, itemView);

        }
    }
}
