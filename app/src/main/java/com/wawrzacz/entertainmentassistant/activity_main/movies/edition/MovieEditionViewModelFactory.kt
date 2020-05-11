package com.wawrzacz.entertainmentassistant.activity_main.movies.edition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MovieEditionViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieEditionViewModel() as T
    }
}