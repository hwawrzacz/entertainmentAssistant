package com.wawrzacz.entertainmentassistant.activity_main.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BrowseViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BrowseViewModel() as T
    }
}