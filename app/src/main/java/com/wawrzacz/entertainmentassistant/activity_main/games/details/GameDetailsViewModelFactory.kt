package com.wawrzacz.entertainmentassistant.activity_main.games.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameDetailsViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GameDetailsViewModel() as T
    }
}