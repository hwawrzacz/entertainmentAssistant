package com.wawrzacz.entertainmentassistant.activity_main.series

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.enums.ItemType
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.data.repositories.MoviesFirebaseRepository
import com.wawrzacz.entertainmentassistant.data.repositories.SeriesFirebaseRepository
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.ui.adapters.ItemPassingViewModel

class SeriesViewModel: ViewModel(), ItemPassingViewModel {
    private val seriesRepository = SeriesFirebaseRepository

    private val _responseToWatchStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseToWatchStatus: LiveData<ResponseStatus> = _responseToWatchStatus

    private val _responseWatchedStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseWatchedStatus: LiveData<ResponseStatus> = _responseWatchedStatus

    private val _responseFavouritesStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseFavouritesStatus: LiveData<ResponseStatus> = _responseFavouritesStatus

    private val _selectedItem = MutableLiveData<CommonListItem?>()
    override val selectedItem: LiveData<CommonListItem?> = _selectedItem

    val foundToWatchSeries: LiveData<List<CommonListItem>?> = Transformations.map(seriesRepository.foundToWatchSeries) {
        _responseToWatchStatus.value = it.responseStatus
        it.items
    }
    val foundWatchedSeries: LiveData<List<CommonListItem>?> = Transformations.map(seriesRepository.foundWatchedSeries) {
        _responseWatchedStatus.value = it.responseStatus
        it.items
    }
    val foundFavouritesSeries: LiveData<List<CommonListItem>?> = Transformations.map(seriesRepository.foundFavouritesSeries) {
        _responseFavouritesStatus.value = it.responseStatus
        it.items
    }

    override fun setSelectedItem(item: CommonListItem?) {
        _selectedItem.value = item
    }

    fun findSeries(section: WatchableSection, query: String?) {
        val targetResponseStatus = when (section) {
            WatchableSection.TO_WATCH -> _responseToWatchStatus
            WatchableSection.WATCHED -> _responseWatchedStatus
            else -> _responseFavouritesStatus
        }

        targetResponseStatus.value = ResponseStatus.IN_PROGRESS
        seriesRepository.findItemsInSection(section, query)
    }

}