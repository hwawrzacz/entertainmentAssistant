package com.wawrzacz.entertainmentassistant.activity_main.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.enums.Section
import com.wawrzacz.entertainmentassistant.data.repositories.DetailedMovieRepository

class DetailsViewModel: ViewModel() {

    private val repository = DetailedMovieRepository

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccessful = MutableLiveData<Boolean>()
    val isSuccessful: LiveData<Boolean> = _isSuccessful

    private val _currentItem = MutableLiveData<DetailedItem?>()

    fun getDetailedItem(id: String): LiveData<DetailedItem?> {
        _isLoading.value = true

        return Transformations.map(repository.getDetailedItem(id)) {
            _isLoading.value = false
            _isSuccessful.value = it != null
            _currentItem.value = it
            it
        }
    }

    fun addItemToFirebaseDatabase(section: Section) {
        if (_currentItem.value != null){
            repository.addMovieToCurrentUser(section, _currentItem.value!!)
        }
    }
}