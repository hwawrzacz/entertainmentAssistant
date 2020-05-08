package com.wawrzacz.entertainmentassistant.activity_main.browse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.repositories.ApiRepository
import com.wawrzacz.entertainmentassistant.data.repositories.MoviesFirebaseRepository
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.ui.adapters.TransitViewModel

class BrowseViewModel: ViewModel(), TransitViewModel {
    private val apiRepository = ApiRepository

    private val _selectedItem = MutableLiveData<CommonListItem?>()
    override val selectedItem: LiveData<CommonListItem?> = _selectedItem

    private val _responseStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseStatus: LiveData<ResponseStatus> = _responseStatus

    val foundItems: LiveData<List<CommonListItem>?> = Transformations.map(MoviesFirebaseRepository.foundAllMovies) {
        _responseStatus.value = it.response
        it.items
    }

    fun findItems(query: String?) {
        if (query != null && query.isNotEmpty()){
            _responseStatus.value = ResponseStatus.IN_PROGRESS
//            apiRepository.findItems(query)
            MoviesFirebaseRepository.findAllMoviesByTitle(query)
        }
    }

    override fun setSelectedItem(item: CommonListItem?) {
        _selectedItem.value = item
    }
}