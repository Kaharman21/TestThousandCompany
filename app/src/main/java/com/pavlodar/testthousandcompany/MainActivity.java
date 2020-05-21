package com.pavlodar.testthousandcompany;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pavlodar.testthousandcompany.Adapter.MovieListAdapter;
import com.pavlodar.testthousandcompany.Common.Common;
import com.pavlodar.testthousandcompany.Database.MovieDatabase;
import com.pavlodar.testthousandcompany.Database.MovieItem;
import com.pavlodar.testthousandcompany.Model.MovieList;
import com.pavlodar.testthousandcompany.Model.MovieListModel;
import com.pavlodar.testthousandcompany.Retrofit.ITestThousandCompanyAPI;
import com.pavlodar.testthousandcompany.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    @BindView(R.id.btn_add)
    Button btn_add;
    @BindView(R.id.btn_clean)
    Button btn_clean;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;


    ITestThousandCompanyAPI testThousandCompanyAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    MovieListAdapter movieListAdapter;

    MovieDatabase movieDatabase;
    List<MovieList> movieList;
    List<MovieItem> movieItemListToInsert;
    MovieItem movieItem;


    @Override
    protected void onDestroy() {
        Log.d("TAG", "onDestroy");
        compositeDisposable.clear();
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();
        initView();
    //    showMovieList();
        swipeRefreshing();
        dataInTheCache();




        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(movieList, new Comparator<MovieList>() {
                    @Override
                    public int compare(MovieList o1, MovieList o2) {
                        return o1.getTitle().compareTo(o2.getTitle());
                    }
                });

                if (movieList.size() >0){
                    movieListAdapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Дурак что ли? Нечего обновлять!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     //   TODO Удаление всех записей
        compositeDisposable.add(movieDatabase.movieDAO().deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer ->{
                }, throwable -> {}));

                movieListAdapter.notifyDataSetChanged();
            }
        });
    }


    private void swipeRefreshing () {
        //TODO Обновдение данных по свайпу
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                movieList.add(new MovieList( "Aaaaaaaa","/rzRwTcFvttcN1ZpX2xv4j3tSdJu.jpg",  77.7));

                movieListAdapter.notifyDataSetChanged();
                collectionsSort();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private void collectionsSort(){
        Collections.sort(movieList, new Comparator<MovieList>() {
            @Override
            public int compare(MovieList o1, MovieList o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });


        if (movieList.size() >0){
            movieListAdapter.notifyDataSetChanged();
        }
        else
        {
            Toast.makeText(MainActivity.this, "Нечего обновлять!", Toast.LENGTH_SHORT).show();
        }
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


                    Toast.makeText(this, "P O L E T E L I", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "S I Z E === "+movieListModel.getItemCount(), Toast.LENGTH_SHORT).show();

                    // TODO передаем список в Адаптер
                    movieList = movieListModel.getMovieLists();
                    movieListAdapter = new MovieListAdapter(this, movieList);
                    recycler_movie_list.setAdapter(movieListAdapter);



                    // TODO добавляю в кэш
                    for (int a=0; a<movieListModel.getItemCount(); a++)
                    {
                        Log.d("TAG", "INSERT +++ ***" + movieListModel.getMovieLists().get(a).getTitle());


                        movieItem = new MovieItem();
                        movieItem.setId(movieListModel.getMovieLists().get(a).getId());
                        movieItem.setPosterPath(movieListModel.getMovieLists().get(a).getPosterPath());
                        movieItem.setTitle(movieListModel.getMovieLists().get(a).getTitle());
                        movieItem.setVoteAverage(movieListModel.getMovieLists().get(a).getVoteAverage());
                        movieItem.setPosition(a);


                        compositeDisposable.add(movieDatabase.movieDAO().insert(movieItem)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    Log.d("TAG", " Добавлено в КЭШшшшш  =============== " + movieItem.getTitle() +"  === "+movieItem.getPosition());
                                }, throwable -> {

                                }));

                    }
                    Log.d("TAG", " --------------------------END INSERT-----------------------------");




                    // Скисок фильмов с КЭШа
                    compositeDisposable.add(movieDatabase.movieDAO().getMovies()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(movieItems -> {
                                Log.d("TAG", " --------------------------START-----------------------------");

                                if (movieItems.size() > 0) {
                                    for (int i = 0; i < movieItems.size(); i++) {
//
                                        Log.d("TAG", "УЖЕ в КЭШшшшш   ======  " + movieItems.get(i).getTitle() + "  pos   " + movieItems.get(i).getPosition());

                                    }
                                }
                                else
                                {
                                    Toast.makeText(this, "ИНЕТ ЕСТЬ. Данные в КЭШе отсутствуют", Toast.LENGTH_SHORT).show();

                                }

                            }, throwable -> {

                            }));

                    collectionsSort();

                }, throwable -> {//TODO/////////////////////// NO Signal/////////////////////


                    Toast.makeText(this, "2 ERROR: {Не отвечает сервак, нет инета} "+ throwable.getMessage(), Toast.LENGTH_SHORT).show();

                    // TODO Показываем из КЭШа
                    showFromCache();


                }));
    }


    private void dataInTheCache ()
    {
        compositeDisposable.add(movieDatabase.movieDAO().getMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieItems ->{

                    // TODO Проверка есть ли данные в КЭШе?
                    if (movieItems.size() > 0) {

                        // TODO Данные в КЭШе есть

                        //TODO Здесь идем на сервер и проверяем актуальны ли данные в КЭШе? (проверяем по количеству)
                        compositeDisposable.add(testThousandCompanyAPI.getMovieList()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(movieListModel -> {

                                    //TODO Если по количетсву записей равны то предполагаем, что данные актуальны и отображаем из КЭШа+++++++++
                                    if (movieListModel.getItemCount() == movieItems.size())
                                    {
                                        showFromCache();
                                        Toast.makeText(this, "Актуальные данные с КЭШа", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(this, "Данные с КЭШа НЕ АКТУАЛЬНЫ", Toast.LENGTH_SHORT).show();

                                        // TODO Данные в КЭШе не актуальны. Удаляем КЭШ и создаем новый
                                        deleteAllFromCache();

                                        // TODO НАДО ПЕРЕПРОВЕРИТЬ
                                        // TODO НАДО ПЕРЕПРОВЕРИТЬ
                                        // TODO НАДО ПЕРЕПРОВЕРИТЬ
                                        // TODO НАДО ПЕРЕПРОВЕРИТЬ
                                        // TODO передаем список в Адаптер
                                        movieList = movieListModel.getMovieLists();
                                        movieListAdapter = new MovieListAdapter(this, movieList);
                                        recycler_movie_list.setAdapter(movieListAdapter);

                                        // TODO добавляем актуальные данные в кэш
                                        for (int i=0; i<movieListModel.getItemCount(); i++)
                                        {
                                            Log.d("TAG", "INSERT +++ ***" + movieListModel.getMovieLists().get(i).getTitle());


                                            movieItem = new MovieItem();
                                            movieItem.setId(movieListModel.getMovieLists().get(i).getId());
                                            movieItem.setPosterPath(movieListModel.getMovieLists().get(i).getPosterPath());
                                            movieItem.setTitle(movieListModel.getMovieLists().get(i).getTitle());
                                            movieItem.setVoteAverage(movieListModel.getMovieLists().get(i).getVoteAverage());
                                            movieItem.setPosition(i);


                                            compositeDisposable.add(movieDatabase.movieDAO().insert(movieItem)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(() -> {
                                                        Log.d("TAG", " Добавлено в КЭШшшшш  =============== " + movieItem.getTitle() +"  === "+movieItem.getPosition());
                                                    }, throwable -> {

                                                    }));

                                        }


                                    }

                                }, throwable -> {
                                    // TODO Инет, либо сервер не работают. Отображаем данные с КЭШа     ++++++++++
                                    showFromCache();
                                    Log.d("TAG", "[testThousandCompanyAPI.getMovieList()] = "+throwable.getMessage());
                                    Toast.makeText(this, "[testThousandCompanyAPI.getMovieList()] = "+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                                }));
                    }
                    else
                    {
                        // TODO Данных в КЭШе нету. Инет не проверен      +++++++++
                        Toast.makeText(this, "Данные отсутствуют", Toast.LENGTH_SHORT).show();

                        compositeDisposable.add(testThousandCompanyAPI.getMovieList()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(movieListModel -> {
                                    // TODO Инет работает, загружаем новые данные в КЭШ. Отображаем данные с сервера++++++++

                                    // TODO передаем список в Адаптер++++++++++
                                    movieList = movieListModel.getMovieLists();
                                    movieListAdapter = new MovieListAdapter(this, movieList);
                                    recycler_movie_list.setAdapter(movieListAdapter);

                                    // TODO добавляем актуальные данные в кэш    +++++++++++
                                    for (int i=0; i<movieListModel.getItemCount(); i++)
                                    {
                                        Log.d("TAG", "INSERT +++ ***" + movieListModel.getMovieLists().get(i).getTitle());


                                        movieItem = new MovieItem();
                                        movieItem.setId(movieListModel.getMovieLists().get(i).getId());
                                        movieItem.setPosterPath(movieListModel.getMovieLists().get(i).getPosterPath());
                                        movieItem.setTitle(movieListModel.getMovieLists().get(i).getTitle());
                                        movieItem.setVoteAverage(movieListModel.getMovieLists().get(i).getVoteAverage());
                                        movieItem.setPosition(i);


                                        compositeDisposable.add(movieDatabase.movieDAO().insert(movieItem)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(() -> {
                                                    Log.d("TAG", " Добавлено в КЭШшшшш  =============== " + movieItem.getTitle() +"  === "+movieItem.getPosition());
                                                }, throwable -> {
                                                    Log.d("TAG", throwable.getMessage());
                                                }));

                                    }

                                }, throwable -> {
                                    // TODO Данных в КЭШе нету. Инет, либо сервер не работают ++++++++
                                    Toast.makeText(this, "Похоже, у Вас проблемы с интернетом", Toast.LENGTH_SHORT).show();

                                }));
                    }

                },throwable -> {

                    Toast.makeText(this, "[movieDatabase.movieDAO().getMovies()] = "+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                }));
    }


    private void showFromCache(){

        // Скисок фильмов с КЭШа
        compositeDisposable.add(movieDatabase.movieDAO().getMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieItems -> {
                    Toast.makeText(this, "movieItems === "+ Common.itemCount, Toast.LENGTH_SHORT).show();


                    // Если в КЭШе есть данные, то отправляем их в Адаптер. Если нету то выводим Тоаст
                    if (movieItems.size() > 0) {
                        Toast.makeText(this, "Данные ЕСТЬ", Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "Данные ЕСТЬ "+ movieItems.size() );

                        for (int i = 0; i < movieItems.size(); i++) {

                            movieList.add(new MovieList(movieItems.get(i).getId(), movieItems.get(i).getTitle(),
                                    movieItems.get(i).getPosterPath(), movieItems.get(i).getVoteAverage()));
                            Log.d("TAG", "ИИИИЗЗЗЗЗ Кешаааа " + movieList.get(i).getTitle() + "   pos   = " + movieItems.get(i).getPosition());
                        }

                        movieListAdapter = new MovieListAdapter(this, movieList);
                        recycler_movie_list.setAdapter(movieListAdapter);
                    }
                    else
                    {
                        Toast.makeText(this, "ИНЕТА НЕТ. Данные отсутствуют", Toast.LENGTH_SHORT).show();
                    }
//

                    Log.d("TAG", "ИИИИЗЗЗЗЗ Кешаааа SIZE *0***  " + movieList.size());
                    Log.d("TAG", "T O TTTTOOoooooooooooooooooooooo *5***  " + movieItems.size());

                    collectionsSort();


                }, throwable1 -> {
                    Toast.makeText(this, "[movieDatabase.movieDAO().getMovies()] = "+throwable1.getMessage(), Toast.LENGTH_SHORT).show();
                }));

    }

    private void deleteAllFromCache() {
        compositeDisposable.add(movieDatabase.movieDAO().deleteAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer ->{
                }, throwable -> {}));

        movieListAdapter.notifyDataSetChanged();
    }


    private void init() {
        testThousandCompanyAPI = RetrofitClient.getInstance(Common.API_THOUSAND_COMPANY_ENDPOINT).create(ITestThousandCompanyAPI.class);
        movieDatabase = MovieDatabase.getInstance(this);
        movieList = new ArrayList<>();
    }

    private void initView() {
        ButterKnife.bind(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_movie_list.setLayoutManager(layoutManager);
    }



}



//     //   TODO Удаление всех записей
//        compositeDisposable.add(movieDatabase.movieDAO().deleteAll()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(integer ->{
//                }, throwable -> {}));



//                    movieList = new ArrayList<>();
//                    movieList.add(new MovieList( "/rzRwTcFvttcN1ZpX2xv4j3tSdJu.jpg", "Thor: Ragnarok", 7.6));
//                    movieList.add(new MovieList( "/c24sv2weTHPsmDa7jEMN0m2P3RT.jpg", "Spider-Man: Homecoming", 7.4));
//                    movieList.add(new MovieList( "/y4MBh0EjBlMuOzv9axM4qJlmhzz.jpg", "Guardians of the Galaxy Vol. 2", 7.6));
//                    movieListAdapter = new MovieListAdapter(this, movieList);
//                    recycler_movie_list.setAdapter(movieListAdapter);