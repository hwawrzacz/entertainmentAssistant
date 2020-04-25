package com.wawrzacz.entertainmentassistant.activity_main.movies.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.MovieSimple
import com.wawrzacz.entertainmentassistant.data.repos.MoviesApiRepository

class MoviesBrowseViewModel: ViewModel() {
    private val moviesRepository = MoviesApiRepository

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasAnyResults = MutableLiveData<Boolean?>()
    val hasAnyResults: LiveData<Boolean?> = _hasAnyResults

    val foundMovies: LiveData<List<MovieSimple>> = Transformations.map(moviesRepository.foundMoviesResponse) {
        _isLoading.value = false
        _hasAnyResults.value = !it.movies.isNullOrEmpty()
        it.movies
    }

    fun findMovies(query: String?) {
        if (query.isNullOrBlank()){
            _hasAnyResults.value = null
        } else {
            _isLoading.value = true
            moviesRepository.findMovies(query)
        }
    }
}