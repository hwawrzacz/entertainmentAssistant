package com.wawrzacz.entertainmentassistant.activity_main.movies.watched

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem
import com.wawrzacz.entertainmentassistant.data.repos.MoviesFirebaseRepository

class MoviesWatchedViewModel: ViewModel() {
    private val moviesRepository = MoviesFirebaseRepository

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    val foundMovies: LiveData<List<UniversalItem>> = Transformations.map(moviesRepository.result) {
        _isLoading.value = false
        it
    }

    fun loadData() {
        _isLoading.value = true
    }

    fun findMovies(query: String) {
        _isLoading.value = true
    }
}