package com.wawrzacz.entertainmentassistant.data.retrofit

import com.wawrzacz.entertainmentassistant.data.model.Movie
import com.wawrzacz.entertainmentassistant.data.repos.ResponseMoviesList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {
    @GET("/?")
    fun findMovies(@Query("s") searchPhrase: String,
                   @Query("apikey") apikey: String): Call<ResponseMoviesList>
    @GET("/")
    fun getMovieById(@Query("i") searchPhrase: String?,
                   @Query("apikey") apikey: String): Call<Movie>
}