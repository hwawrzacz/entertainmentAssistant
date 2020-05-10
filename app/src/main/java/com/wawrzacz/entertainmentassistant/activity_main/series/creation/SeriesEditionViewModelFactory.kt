package com.wawrzacz.entertainmentassistant.activity_main.series.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SeriesEditionViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SeriesEditionViewModel() as T
    }
}