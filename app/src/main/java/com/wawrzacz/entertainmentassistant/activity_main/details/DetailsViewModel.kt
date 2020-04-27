package com.wawrzacz.entertainmentassistant.activity_main.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem
import com.wawrzacz.entertainmentassistant.data.repos.ApiRepository

class DetailsViewModel: ViewModel() {

    private val apiRepository = ApiRepository

    private val _selectedItem = MutableLiveData<DetailedItem>()
    val selectedItem: LiveData<DetailedItem> = _selectedItem

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isItemLoading: LiveData<Boolean> = _isLoading

    fun getItem(id: String): LiveData<DetailedItem> {
        return apiRepository.getItem(id)
    }
}