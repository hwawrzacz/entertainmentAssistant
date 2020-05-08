package com.wawrzacz.entertainmentassistant.activity_main.browse

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.repositories.ApiRepository
import com.wawrzacz.entertainmentassistant.data.repositories.BrowseRepository
import com.wawrzacz.entertainmentassistant.data.repositories.MoviesFirebaseRepository
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.ui.adapters.TransitViewModel

class BrowseViewModel: ViewModel(), TransitViewModel {
    private val browseRepository = BrowseRepository

    private val _selectedItem = MutableLiveData<CommonListItem?>()
    override val selectedItem: LiveData<CommonListItem?> = _selectedItem

    private val _responseStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseStatus: LiveData<ResponseStatus> = _responseStatus

    private val query = MutableLiveData<String>()

    val foundItems: LiveData<ArrayList<CommonListItem>?> =
    Transformations.switchMap(query) { query ->
        Transformations.map(browseRepository.findItems(query)){
            _responseStatus.value = it.responseStatus
            it.items
        }
    }

    fun findItems(query: String?) {
        if (query != null && query.isNotEmpty()){
            _responseStatus.value = ResponseStatus.IN_PROGRESS
//            apiRepository.findItems(query)
//            MoviesFirebaseRepository.findAllMoviesByTitle(query)
            browseRepository.findItems(query)
        }
    }

    fun setQuery(query: String?) {
        if (!query.isNullOrBlank()) {
            this.query.value = query
        }
    }

    override fun setSelectedItem(item: CommonListItem?) {
        _selectedItem.value = item
    }
}