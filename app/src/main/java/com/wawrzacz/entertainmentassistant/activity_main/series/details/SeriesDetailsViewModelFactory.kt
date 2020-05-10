package com.wawrzacz.entertainmentassistant.activity_main.series.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SeriesDetailsViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SeriesDetailsViewModel() as T
    }
}