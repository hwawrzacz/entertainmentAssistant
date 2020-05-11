package com.wawrzacz.entertainmentassistant.activity_main.games

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.enums.ItemType
import com.wawrzacz.entertainmentassistant.data.enums.PlayableSection
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.repositories.GamesFirebaseRepository
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.ui.adapters.ItemPassingViewModel

class GamesViewModel: ViewModel(), ItemPassingViewModel {
    private val gamesRepository = GamesFirebaseRepository

    private val _responseToWatchStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseToWatchStatus: LiveData<ResponseStatus> = _responseToWatchStatus

    private val _responseWatchedStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseWatchedStatus: LiveData<ResponseStatus> = _responseWatchedStatus

    private val _responseFavouritesStatus = MutableLiveData(ResponseStatus.NOT_INITIALIZED)
    val responseFavouritesStatus: LiveData<ResponseStatus> = _responseFavouritesStatus

    private val _selectedItem = MutableLiveData<CommonListItem?>()
    override val selectedItem: LiveData<CommonListItem?> = _selectedItem

    val foundToWatchGames: LiveData<List<CommonListItem>?> = Transformations.map(gamesRepository.foundToPlayGames) {
        _responseToWatchStatus.value = it.responseStatus
        it.items
    }
    val foundWatchedGames: LiveData<List<CommonListItem>?> = Transformations.map(gamesRepository.foundPlayedGames) {
        _responseWatchedStatus.value = it.responseStatus
        it.items
    }
    val foundFavouritesGames: LiveData<List<CommonListItem>?> = Transformations.map(gamesRepository.foundFavouritesGames) {
        _responseFavouritesStatus.value = it.responseStatus
        it.items
    }

    override fun setSelectedItem(item: CommonListItem?) {
        _selectedItem.value = item
    }

    fun findGames(section: PlayableSection, query: String?) {
        val targetResponseStatus = when (section) {
            PlayableSection.TO_PLAY -> _responseToWatchStatus
            PlayableSection.PLAYED -> _responseWatchedStatus
            PlayableSection.FAVOURITES -> _responseFavouritesStatus
        }

        targetResponseStatus.value = ResponseStatus.IN_PROGRESS
        gamesRepository.findItemsInSection(ItemType.GAME, section, query)
    }

}