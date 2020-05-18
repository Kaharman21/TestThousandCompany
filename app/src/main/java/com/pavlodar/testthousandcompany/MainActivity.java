package com.pavlodar.testthousandcompany;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pavlodar.testthousandcompany.Adapter.MovieListAdapter;
import com.pavlodar.testthousandcompany.Common.Common;
import com.pavlodar.testthousandcompany.Database.MovieDatabase;
import com.pavlodar.testthousandcompany.Model.MovieList;
import com.pavlodar.testthousandcompany.Model.MovieListModel;
import com.pavlodar.testthousandcompany.Retrofit.ITestThousandCompanyAPI;
import com.pavlodar.testthousandcompany.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_movie_list)
    RecyclerView recycler_movie_list;
    @BindView(R.id.image)
    ImageView image;

    ITestThousandCompanyAPI testThousandCompanyAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    MovieListAdapter movieListAdapter;

    MovieDatabase movieDatabase;
    List<MovieList> movieList;


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();
        initView();
      //  showMovieDetails();
        showMovieList();
    }


    private void showMovieList(){
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500/wwemzKWzjKYJFfCeiB57q3r4Bcm.png")
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(image);
        compositeDisposable.add(testThousandCompanyAPI.getMovieList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieListModel -> {

       //                 text.setText(movieListModel.getName());

                        // ВСЕ ЧТО ВНИЗУ РАБОТАЕТ ПРОСТО ПОКА ЛИШНЕЕ
           //             text2.setText(String.valueOf(itemsModel.getItemCount()));
//                    StringBuilder sb = new StringBuilder();
//                    sb.append(movieListModel.getMovieLists().get(21).getTitle());
//                    sb.append(movieListModel.getMovieLists().get(2).getTitle());
//                    sb.append(movieListModel.getMovieLists().get(1).getTitle());
//                    text2.setText(sb);

        //            text2.setText(movieListModel.getMovieLists().toString());

                    movieListAdapter = new MovieListAdapter(this, movieListModel.getMovieLists());
                    recycler_movie_list.setAdapter(movieListAdapter);

//                    Picasso.get().load("https://image.tmdb.org/t/p/w500/rzRwTcFvttcN1ZpX2xv4j3tSdJu.jpg")
//                            .into(image);
                }, throwable -> {

                    movieList = new ArrayList<>();
                    movieList.add(new MovieList( "/rzRwTcFvttcN1ZpX2xv4j3tSdJu.jpg", "Thor: Ragnarok", 7.6));
                    movieList.add(new MovieList( "/c24sv2weTHPsmDa7jEMN0m2P3RT.jpg", "Spider-Man: Homecoming", 7.4));
                    movieList.add(new MovieList( "/y4MBh0EjBlMuOzv9axM4qJlmhzz.jpg", "Guardians of the Galaxy Vol. 2", 7.6));
                    movieListAdapter = new MovieListAdapter(this, movieList);
                    recycler_movie_list.setAdapter(movieListAdapter);

                    Toast.makeText(this, "2 ERROR: {Не отвечает сервак, нет инета} "+ throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }


    private void showMovieDetails(){
        compositeDisposable.add(testThousandCompanyAPI.getMovieDetails(Common.API_KEY)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(movieDetails -> {

            //    text.setText(movieDetails.getStatus());

        }, throwable -> {

                    Toast.makeText(this, "ERROR 1", Toast.LENGTH_SHORT).show();
        }
        ));
    }



    private void init() {
        testThousandCompanyAPI = RetrofitClient.getInstance(Common.API_THOUSAND_COMPANY_ENDPOINT).create(ITestThousandCompanyAPI.class);

   //     movieDatabase = MovieDatabase.getInstance(this);
    }

    private void initView() {
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_movie_list.setLayoutManager(layoutManager);
    }
}
