package com.wawrzacz.entertainmentassistant.activity_main.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.repositories.ApiRepository
import com.wawrzacz.entertainmentassistant.ui.adapters.TransitViewModel

class BrowseViewModel: ViewModel(), TransitViewModel {
    private val apiRepository = ApiRepository

    private val _selectedItem = MutableLiveData<CommonListItem?>()
    override val selectedItem: LiveData<CommonListItem?> = _selectedItem

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _hasAnyResults = MutableLiveData<Boolean?>()
    val hasAnyResults: LiveData<Boolean?> = _hasAnyResults

    private val _isSuccessful = MutableLiveData<Boolean?>()
    val isSuccessful: LiveData<Boolean?> = _isSuccessful

    val foundItems: LiveData<List<CommonListItem>?> = Transformations.map(apiRepository.foundItemsResponse) {
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

    override fun setSelectedItem(item: CommonListItem?) {
        _selectedItem.value = item
    }
}