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
    val isListLoading: LiveData<Boolean> = _isLoading

    private val _hasAnyResults = MutableLiveData<Boolean?>()
    val hasAnyResults: LiveData<Boolean?> = _hasAnyResults

    val foundItems: LiveData<List<UniversalItem>> = Transformations.map(apiRepository.foundItemsResponse) {
        _isLoading.value = false
        _hasAnyResults.value = !it.items.isNullOrEmpty()
        it.items
    }

    fun findItems(query: String?) {
        if (query.isNullOrBlank()){
            _hasAnyResults.value = null
        } else {
            _isLoading.value = true
            apiRepository.findItems(query)
        }
    }

    fun setSelectedItem(item: UniversalItem) {
        _selectedItemId.value = item
    }
}