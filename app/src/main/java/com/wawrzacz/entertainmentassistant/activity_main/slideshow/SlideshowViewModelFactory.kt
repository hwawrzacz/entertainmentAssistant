package com.wawrzacz.entertainmentassistant.activity_main.slideshow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SlideshowViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SlideshowViewModel() as T
    }
}