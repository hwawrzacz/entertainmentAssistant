package com.wawrzacz.entertainmentassistant.data.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.wawrzacz.entertainmentassistant.data.retrofit.MoviesService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MoviesApiRepository {
    private val retrofit: Retrofit
    private val service: MoviesService
    private val BASE_URL = "http://www.omdbapi.com" //Resources.getSystem().getString(R.string.retrofit_base_url)
    private val API_KEY = "373cac09" //Resources.getSystem().getString(R.string.retrofit_api_key)

    private val _searchedMoviesResponse = MutableLiveData<ResponseMoviesList>()
    val foundMoviesResponse: LiveData<ResponseMoviesList> = _searchedMoviesResponse

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()
            ))
            .build()

        service = retrofit.create(MoviesService::class.java)
    }

    fun findMovies(query: String) {
        Log.i("schab query", query)

        service.findMovies(query, apikey = API_KEY)
            .enqueue(object: Callback<ResponseMoviesList> {
                override fun onResponse(call: Call<ResponseMoviesList>, response: Response<ResponseMoviesList>) {
                    if (response.isSuccessful) {
                        _searchedMoviesResponse.value = response.body()
                        Log.i("schab success", "a ${response.body()?.movies.toString()}")
                    }
                    else {
                        Log.i("schab not success", response.message())
                    }
                }

                override fun onFailure(call: Call<ResponseMoviesList>, t: Throwable) {
                    Log.i("schab fail", t.message)
                }
            })
    }
}