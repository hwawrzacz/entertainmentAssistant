package com.wawrzacz.entertainmentassistant.activity_main.series.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.enums.ItemType
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.data.repositories.DetailsRepository
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus

class SeriesDetailsViewModel: ViewModel() {

    private val repository = DetailsRepository

    private val _responseStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseStatus: LiveData<ResponseStatus> = _responseStatus

    private val _currentItem = MutableLiveData<DetailedItem?>()
    val currentItem: LiveData<DetailedItem?> = _currentItem

    val isSeriesInFavourite: (id: String) -> LiveData<Boolean> = { id ->
        Transformations.map(repository.isSeriesInSection(id, WatchableSection.FAVOURITES)) { it }
    }

    val isSeriesInWatched: (id: String) -> LiveData<Boolean> = { id ->
        Transformations.map(repository.isSeriesInSection(id, WatchableSection.WATCHED)) { it }
    }

    val isSeriesInToWatch: (id: String) -> LiveData<Boolean> = { id ->
        Transformations.map(repository.isSeriesInSection(id, WatchableSection.TO_WATCH)) { it }
    }

    fun getDetailedItem(id: String): LiveData<DetailedItem?> {
        _responseStatus.value = ResponseStatus.IN_PROGRESS

        return Transformations.map(repository.getDetailedSeries(id)) {
            _responseStatus.value = it.responseStatus
            _currentItem.value = it.item
            it.item
        }
    }

    fun toggleItemSection(section: WatchableSection) {
        if (_currentItem.value != null){
            repository.toggleSeriesSection(section, _currentItem.value!!)
        }
    }
}