package com.wawrzacz.entertainmentassistant.activity_main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.data.repositories.MoviesFirebaseRepository
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.ui.adapters.TransitViewModel

class MoviesViewModel: ViewModel(), TransitViewModel {
    private val moviesRepository = MoviesFirebaseRepository

    private val _responseToWatchStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseToWatchStatus: LiveData<ResponseStatus> = _responseToWatchStatus

    private val _responseWatchedStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseWatchedStatus: LiveData<ResponseStatus> = _responseWatchedStatus

    private val _responseFavouritesStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseFavouritesStatus: LiveData<ResponseStatus> = _responseFavouritesStatus

    private val _selectedItem = MutableLiveData<CommonListItem?>()
    override val selectedItem: LiveData<CommonListItem?> = _selectedItem

    val foundToWatchMovies: LiveData<List<CommonListItem>?> = Transformations.map(moviesRepository.foundToWatchMovies) {
        _responseToWatchStatus.value = it.responseStatus
        it.items
    }
    val foundWatchedMovies: LiveData<List<CommonListItem>?> = Transformations.map(moviesRepository.foundWatchedMovies) {
        _responseWatchedStatus.value = it.responseStatus
        it.items
    }
    val foundFavouritesMovies: LiveData<List<CommonListItem>?> = Transformations.map(moviesRepository.foundFavouritesMovies) {
        _responseFavouritesStatus.value = it.responseStatus
        it.items
    }

    override fun setSelectedItem(item: CommonListItem?) {
        _selectedItem.value = item
    }

    fun findMovies(section: WatchableSection, query: String?) {
        val targetResponseStatus = when (section) {
            WatchableSection.TO_WATCH -> _responseToWatchStatus
            WatchableSection.WATCHED -> _responseWatchedStatus
            else -> _responseFavouritesStatus
        }

        targetResponseStatus.value = ResponseStatus.IN_PROGRESS
        moviesRepository.findItemsInSection(section, query)
    }

}