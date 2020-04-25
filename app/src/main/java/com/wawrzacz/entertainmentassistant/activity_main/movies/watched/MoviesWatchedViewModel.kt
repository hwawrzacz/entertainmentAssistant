package com.wawrzacz.entertainmentassistant.activity_main.movies.watched

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.Movie
import com.wawrzacz.entertainmentassistant.data.model.MovieSimple
import com.wawrzacz.entertainmentassistant.data.repos.MoviesRepository
import com.wawrzacz.entertainmentassistant.data.repos.ResponseMoviesList

class MoviesWatchedViewModel: ViewModel() {
    private val moviesRepository = MoviesRepository

    val foundMovies: LiveData<List<MovieSimple>> = Transformations.map(moviesRepository.foundMoviesResponse) {
        _isLoading.value = false
        it.movies
    }
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

//    fun loadData(query: String?) {
//        _isLoading.value = true
//        moviesRepository.loadWatchedMovies(query)
//    }

    fun findMovies(query: String) {
        _isLoading.value = true
        moviesRepository.findMovies(query)
    }
}