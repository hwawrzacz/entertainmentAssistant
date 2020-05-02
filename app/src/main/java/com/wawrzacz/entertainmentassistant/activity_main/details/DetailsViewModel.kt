package com.wawrzacz.entertainmentassistant.activity_main.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.repos.ApiRepository
import com.wawrzacz.entertainmentassistant.data.repos.MoviesFirebaseRepository

class DetailsViewModel: ViewModel() {

    private val apiRepository = ApiRepository
    private val firebaseRepository = MoviesFirebaseRepository

    private val _currentItem = MutableLiveData<DetailedItem?>()

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccessful = MutableLiveData<Boolean>()
    val isSuccessful: LiveData<Boolean> = _isSuccessful

    fun getDetailedItem(id: String): LiveData<DetailedItem> {
        _isLoading.value = true
        return Transformations.map(apiRepository.getItem(id)){
            Log.i("schab", "tranformation")
            if (it === null) _isSuccessful.value = false
            else if (it.response.toBoolean()) _isSuccessful.value = it.response.toBoolean()

            _isLoading.value = false
            it
        }
    }

    fun addItemToFirebaseDatabase(section: String) {
        if (_currentItem.value != null){
            Log.i("schab", "add item ${_currentItem.value}")
            firebaseRepository.addMovieToCurrentUser(section, _currentItem.value)
        }
    }
}