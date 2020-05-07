package com.wawrzacz.entertainmentassistant.activity_main.movies.movie_creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MovieCreationViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieCreationViewModel() as T
    }
}