package com.wawrzacz.entertainmentassistant.activity_main.games.edition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.enums.ItemType
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.response_statuses.FormValidationState
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.repositories.GamesFirebaseRepository
import java.util.*

class GameEditionViewModel: ViewModel() {

    private val repository = GamesFirebaseRepository

    //#region Validation fields
    private val _formValidity = MutableLiveData(false)
    val formValidity: LiveData<Boolean> = _formValidity

    private val _titleValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val titleValidity: LiveData<FormValidationState> = _titleValidity

    private val _yearValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val yearValidity: LiveData<FormValidationState> = _yearValidity

    private val _directorValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val directorValidity: LiveData<FormValidationState> = _directorValidity

    private val _plotValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val plotValidity: LiveData<FormValidationState> = _plotValidity

    private val game = DetailedItem(type = ItemType.GAME)

    private val _hasChanges = MutableLiveData(false)
    val hasChanges: LiveData<Boolean> = _hasChanges

    val gameCreationStatus: LiveData<ResponseStatus> = Transformations.map(repository.gameEditionStatus) {
        if (it == ResponseStatus.SUCCESS)
            _hasChanges.value = false
        it
    }

    fun setGameId(value: String) {
        this.game.id = value
    }

    fun onPosterUrlChanged(value: String) {
        game.posterUrl = value
    }

    fun onTitleChanged(value: String) {
        if (value.isBlank())
            _titleValidity.value = FormValidationState.FIELD_EMPTY
        else {
            _titleValidity.value = FormValidationState.VALID
            game.title = value
            game.queryTitle = value.toLowerCase(Locale.ROOT)
        }
        checkFormValidity()
    }

    fun onYearChanged(value: String?) {
        if (value.isNullOrBlank())
            _yearValidity.value = FormValidationState.FIELD_EMPTY
        else {
            _yearValidity.value = FormValidationState.VALID
            game.year = value
        }
        checkFormValidity()
    }

    fun onDirectorChanged(value: String) {
        if (value.isBlank())
            _directorValidity.value = FormValidationState.FIELD_EMPTY
        else {
            _directorValidity.value = FormValidationState.VALID
            game.director = value
        }
        checkFormValidity()
    }

    fun onPlotChanged(value: String) {
        if (value.isBlank())
            _plotValidity.value = FormValidationState.FIELD_EMPTY
        else {
            _plotValidity.value = FormValidationState.VALID
            game.plot = value
        }
        checkFormValidity()
    }

    fun onGenreChanged(value: String) {
        game.genre = value
    }

    fun createGame() {
        if (_formValidity.value == true) {
            repository.createItem(game)
        }
    }

    fun updateGame() {
        if (_formValidity.value == true) {
            repository.updateItem(game)
        }
    }

    private fun checkFormValidity() {
        _hasChanges.value = true
        _formValidity.value =
            _titleValidity.value == FormValidationState.VALID &&
            yearValidity.value == FormValidationState.VALID &&
            _directorValidity.value == FormValidationState.VALID &&
            _plotValidity.value == FormValidationState.VALID
    }
}