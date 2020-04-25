package com.wawrzacz.entertainmentassistant.activity_main.movies.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MoviesBrowseViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MoviesBrowseViewModel() as T
    }
}