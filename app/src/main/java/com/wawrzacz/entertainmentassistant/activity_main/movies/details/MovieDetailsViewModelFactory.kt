package com.wawrzacz.entertainmentassistant.activity_main.movies.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MovieDetailsViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieDetailsViewModel() as T
    }
}