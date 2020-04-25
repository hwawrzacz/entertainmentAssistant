package com.wawrzacz.entertainmentassistant.activity_main.movies.watched

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MoviesWatchedViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MoviesWatchedViewModel() as T
    }
}