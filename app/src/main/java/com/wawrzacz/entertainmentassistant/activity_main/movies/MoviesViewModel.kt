package com.wawrzacz.entertainmentassistant.activity_main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem
import com.wawrzacz.entertainmentassistant.data.repos.MoviesFirebaseRepository

class MoviesViewModel: ViewModel() {
    private val moviesRepository = MoviesFirebaseRepository

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    val foundToWatchMovies: LiveData<List<UniversalItem>> = Transformations.map(moviesRepository.foundToWatchMovies) {
        _isLoading.value = false
        it
    }
    val foundWatchedMovies: LiveData<List<UniversalItem>> = Transformations.map(moviesRepository.foundWatchedMovies) {
        _isLoading.value = false
        it
    }
    val foundFavouritesMovies: LiveData<List<UniversalItem>> = Transformations.map(moviesRepository.foundFavouritesMovies) {
        _isLoading.value = false
        it
    }

    fun loadData() {
        _isLoading.value = true
    }

    fun findMovies(section: String, query: String) {
        _isLoading.value = true
        moviesRepository.getAllWatchedMovies(section)
    }
}