package com.wawrzacz.entertainmentassistant.activity_main.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem
import com.wawrzacz.entertainmentassistant.data.repos.ApiRepository

class BrowseViewModel: ViewModel() {
    private val apiRepository = ApiRepository

    private val _selectedItemId = MutableLiveData<UniversalItem>()
    val selectedItemId: LiveData<UniversalItem> = _selectedItemId

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasAnyResults = MutableLiveData<Boolean?>()
    val hasAnyResults: LiveData<Boolean?> = _hasAnyResults

    private val _isSuccessful = MutableLiveData<Boolean?>()
    val isSuccessful: LiveData<Boolean?> = _isSuccessful

    val foundItems: LiveData<List<UniversalItem>?> = Transformations.map(apiRepository.foundItemsResponse) {
        _isLoading.value = false
        if (it == null) {
            _isSuccessful.value = false
        } else {
            _hasAnyResults.value = it.response
        }
        it?.items
    }

    fun findItems(query: String?) {
        if (query.isNullOrBlank()){
            _isSuccessful.value = null
        } else {
            _isLoading.value = true
            apiRepository.findItems(query)
        }
    }

    fun setSelectedItem(item: UniversalItem) {
        _selectedItemId.value = item
    }
}