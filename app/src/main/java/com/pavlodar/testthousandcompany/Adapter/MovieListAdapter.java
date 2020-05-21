package com.pavlodar.testthousandcompany.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pavlodar.testthousandcompany.Common.Common;
import com.pavlodar.testthousandcompany.Database.MovieDatabase;
import com.pavlodar.testthousandcompany.Database.MovieItem;
import com.pavlodar.testthousandcompany.Model.MovieList;
import com.pavlodar.testthousandcompany.R;
import com.pavlodar.testthousandcompany.Retrofit.ITestThousandCompanyAPI;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MyViewHolder> {

    Context context;
    List<MovieList> movieList;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ITestThousandCompanyAPI testThousandCompanyAPI;

    MovieDatabase movieDatabase;

    List<MovieItem> movieItems;
    boolean internet;




    public MovieListAdapter(Context context, List<MovieList> movieList) {
        this.context = context;
        this.movieList = movieList;
        movieDatabase = MovieDatabase.getInstance(context);
    }




    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_movie_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (movieList.size() > 0) {
            Log.d("TAG", "Adapter = " + position);
            holder.txt_vote_avarage.setText(String.valueOf(movieList.get(position).getVoteAverage()));
            holder.txt_favorite.setText(movieList.get(position).getTitle());

            Log.d("TAG", "Movie list size A D A P T E E R ==== " + movieList.size());

            Picasso.get().load("https://image.tmdb.org/t/p/w500/" + movieList.get(position).getPosterPath())
                    .into(holder.img_poster);

            Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500/" + movieList.get(position).getPosterPath())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(holder.img_poster_glide);


//        MovieItem movieItem = new MovieItem();
//        movieItem.setId(movieList.get(position).getId());
//        movieItem.setPosterPath(movieList.get(position).getPosterPath());
//        movieItem.setTitle(movieList.get(position).getTitle());
//        movieItem.setVoteAverage(movieList.get(position).getVoteAverage());
//
//
//        compositeDisposable.add(movieDatabase.movieDAO().insert(movieItem)
//        .subscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(() -> {
//
//        }, throwable -> {
//
//        }));

            // Добавление в БД для кэша
//        movieDatabase.movieDAO().getMovies();
        }
        else
        {
            Toast.makeText(context, "Значения отсутствуют", Toast.LENGTH_SHORT).show();
        }
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
        @BindView(R.id.img_poster_glide)
        ImageView img_poster_glide;


        Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this, itemView);

        }
    }
}
