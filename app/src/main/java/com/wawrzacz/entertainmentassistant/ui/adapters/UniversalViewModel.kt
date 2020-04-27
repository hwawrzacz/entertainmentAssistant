package com.wawrzacz.entertainmentassistant.ui.adapters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface UniversalViewModel {
    val _selectedItemId: MutableLiveData<String>
    val selectedItemId: LiveData<String>

    fun toggleFavourite(id: String)
}