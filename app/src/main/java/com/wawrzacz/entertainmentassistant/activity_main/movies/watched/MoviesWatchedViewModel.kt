package com.wawrzacz.entertainmentassistant.activity_main.movies.watched

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.Movie
import com.wawrzacz.entertainmentassistant.data.repos.MoviesRepository

class MoviesWatchedViewModel: ViewModel() {
    private val moviesRepository = MoviesRepository

    val moviesWatched: LiveData<List<Movie>> = Transformations.map(moviesRepository.watchedMovies) { it }

    fun loadData(query: String?) {
        moviesRepository.loadWatchedMovies(query)
    }
}