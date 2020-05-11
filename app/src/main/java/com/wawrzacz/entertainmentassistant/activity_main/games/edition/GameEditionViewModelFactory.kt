package com.wawrzacz.entertainmentassistant.activity_main.games.edition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameEditionViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GameEditionViewModel() as T
    }
}