package com.wawrzacz.entertainmentassistant.activity_main.games.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.enums.PlayableSection
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.repositories.DetailsRepository
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus

class GameDetailsViewModel: ViewModel() {

    private val repository = DetailsRepository

    private val _responseStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseStatus: LiveData<ResponseStatus> = _responseStatus

    private val _currentItem = MutableLiveData<DetailedItem?>()
    val currentItem: LiveData<DetailedItem?> = _currentItem

    val isGameInFavourite: (id: String) -> LiveData<Boolean> = { id ->
        Transformations.map(repository.isGameInSection(id, PlayableSection.FAVOURITES)) { it }
    }

    val isGameInPlayed: (id: String) -> LiveData<Boolean> = { id ->
        Transformations.map(repository.isGameInSection(id, PlayableSection.PLAYED)) { it }
    }

    val isGameInToPlay: (id: String) -> LiveData<Boolean> = { id ->
        Transformations.map(repository.isGameInSection(id, PlayableSection.TO_PLAY)) { it }
    }

    fun getDetailedItem(id: String): LiveData<DetailedItem?> {
        _responseStatus.value = ResponseStatus.IN_PROGRESS

        return Transformations.map(repository.getDetailedGame(id)) {
            _responseStatus.value = it.responseStatus
            _currentItem.value = it.item
            it.item
        }
    }

    fun toggleItemSection(section: PlayableSection) {
        if (_currentItem.value != null){
            repository.toggleGameSection(section, _currentItem.value!!)
        }
    }
}