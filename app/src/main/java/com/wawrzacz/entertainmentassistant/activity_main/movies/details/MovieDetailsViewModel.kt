package com.wawrzacz.entertainmentassistant.activity_main.movies.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.enums.ItemType
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.data.repositories.DetailsRepository
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus

class MovieDetailsViewModel: ViewModel() {

    private val repository = DetailsRepository

    private val _responseStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseStatus: LiveData<ResponseStatus> = _responseStatus

    private val _currentItem = MutableLiveData<DetailedItem?>()
    val currentItem: LiveData<DetailedItem?> = _currentItem

    val isMovieInFavourite: (id: String) -> LiveData<Boolean> = { id ->
        Transformations.map(repository.isMovieInSection(id,WatchableSection.FAVOURITES)) { it }
    }

    val isMovieInWatched: (id: String) -> LiveData<Boolean> = { id ->
        Transformations.map(repository.isMovieInSection(id, WatchableSection.WATCHED)) { it }
    }

    val isMovieInToWatch: (id: String) -> LiveData<Boolean> = { id ->
        Transformations.map(repository.isMovieInSection(id, WatchableSection.TO_WATCH)) { it }
    }

    fun getDetailedItem(id: String): LiveData<DetailedItem?> {
        _responseStatus.value = ResponseStatus.IN_PROGRESS

        return Transformations.map(repository.getDetailedMovie(id)) {
            _responseStatus.value = it.responseStatus
            _currentItem.value = it.item
            it.item
        }
    }

    fun toggleItemSection(section: WatchableSection) {
        if (_currentItem.value != null){
            repository.toggleMovieSection(section, _currentItem.value!!)
        }
    }
}