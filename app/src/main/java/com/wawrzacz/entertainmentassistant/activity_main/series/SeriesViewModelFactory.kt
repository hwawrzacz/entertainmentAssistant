package com.wawrzacz.entertainmentassistant.activity_main.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SeriesViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SeriesViewModel() as T
    }
}