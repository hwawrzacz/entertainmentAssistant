package com.wawrzacz.entertainmentassistant.activity_main.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GalleryViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GalleryViewModel() as T
    }
}