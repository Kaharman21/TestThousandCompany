package com.pavlodar.testthousandcompany;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pavlodar.testthousandcompany.Adapter.MovieListAdapter;
import com.pavlodar.testthousandcompany.Common.Common;
import com.pavlodar.testthousandcompany.Retrofit.ITestThousandCompanyAPI;
import com.pavlodar.testthousandcompany.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycler_movie_list)
    RecyclerView recycler_movie_list;

    ITestThousandCompanyAPI testThousandCompanyAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    MovieListAdapter movieListAdapter;


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
                    Toast.makeText(this, "2 ERROR: showMovieList "+ throwable.getMessage(), Toast.LENGTH_SHORT).show();
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
    }

    private void initView() {
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_movie_list.setLayoutManager(layoutManager);
    }
}
