package com.pavlodar.testthousandcompany.Retrofit;

import com.pavlodar.testthousandcompany.Model.MovieListModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ITestThousandCompanyAPI {

    // Можно указывать без language=en-US


//    @GET("3/list/1")
//    Observable<MovieListModel> getMovieList(@Query("api_key") String apiKey);

    @GET("3/list/1?api_key=2914a701c39e5d0b353b19e9a6daf196&language=en-US")
    Observable<MovieListModel> getMovieList();

    // РАБОТАЕТ
//    @GET("3/movie/33?api_key=2914a701c39e5d0b353b19e9a6daf196")
//    Observable<MovieDetails> getMovieDetails();
}
