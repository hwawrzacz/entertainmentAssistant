package com.wawrzacz.entertainmentassistant.activity_main.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem
import com.wawrzacz.entertainmentassistant.data.repos.ApiRepository

class DetailsViewModel: ViewModel() {

    private val apiRepository = ApiRepository

    private val _itemId = MutableLiveData<String>()

    private val _selectedItem = MutableLiveData<DetailedItem?>()
    val selectedItem: LiveData<DetailedItem?> = _selectedItem

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccessful = MutableLiveData<Boolean>()
    val isSuccessful: LiveData<Boolean> = _isSuccessful

    fun getItem(id: String): LiveData<DetailedItem> {
        _isLoading.value = true
        return Transformations.map(apiRepository.getItem(id)){
            if (it === null) _isSuccessful.value = false
            else if (it.response.toBoolean()) _isSuccessful.value = it.response.toBoolean()

            _isLoading.value = false
            it
        }
    }
}