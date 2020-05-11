package com.wawrzacz.entertainmentassistant.activity_main.series.edition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.enums.ItemType
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.response_statuses.FormValidationState
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.repositories.SeriesFirebaseRepository
import java.util.*

class SeriesEditionViewModel: ViewModel() {

    private val repository = SeriesFirebaseRepository

    //#region Validation fields
    private val _formValidity = MutableLiveData(false)
    val formValidity: LiveData<Boolean> = _formValidity

    private val _titleValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val titleValidity: LiveData<FormValidationState> = _titleValidity

    private val _yearValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val yearValidity: LiveData<FormValidationState> = _yearValidity

    private val _seasonsValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val seasonsValidity: LiveData<FormValidationState> = _seasonsValidity

    private val _writerValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val writerValidity: LiveData<FormValidationState> = _writerValidity

    private val _plotValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val plotValidity: LiveData<FormValidationState> = _plotValidity

    private val series = DetailedItem(type = ItemType.SERIES)

    private val _hasChanges = MutableLiveData(false)
    val hasChanges: LiveData<Boolean> = _hasChanges

    val seriesCreationStatus: LiveData<ResponseStatus> = Transformations.map(repository.seriesEditionStatus) {
        if (it == ResponseStatus.SUCCESS)
            _hasChanges.value = false
        it
    }

    fun setSeriesId(value: String) {
        this.series.id = value
    }

    fun onPosterUrlChanged(value: String) {
        series.posterUrl = value
    }

    fun onTitleChanged(value: String) {
        if (value.isBlank())
            _titleValidity.value = FormValidationState.FIELD_EMPTY
        else {
            _titleValidity.value = FormValidationState.VALID
            series.title = value
            series.queryTitle = value.toLowerCase(Locale.ROOT)
        }
        checkFormValidity()
    }

    fun onYearChanged(value: String?) {
        if (value.isNullOrBlank())
            _yearValidity.value = FormValidationState.FIELD_EMPTY
        else {
            val numberValue = value.toInt()
            if (numberValue < 1895)
                _yearValidity.value = FormValidationState.YEAR_BEFORE_CINEMATOGRAPHY
            else {
                _yearValidity.value = FormValidationState.VALID
                series.year = value
            }
        }
        checkFormValidity()
    }

    fun onWriterChanged(value: String) {
        if (value.isBlank())
            _writerValidity.value = FormValidationState.FIELD_EMPTY
        else {
            _writerValidity.value = FormValidationState.VALID
            series.writer = value
        }
        checkFormValidity()
    }

    fun onPlotChanged(value: String) {
        if (value.isBlank())
            _plotValidity.value = FormValidationState.FIELD_EMPTY
        else {
            _plotValidity.value = FormValidationState.VALID
            series.plot = value
        }
        checkFormValidity()
    }

    fun onSeasonsChanged(value: String) {
        series.totalSeasons = value
    }

    fun onGenreChanged(value: String) {
        series.genre = value
    }

    fun createSeries() {
        if (_formValidity.value == true) {
            repository.createItem(series)
        }
    }

    fun updateSeries() {
        if (_formValidity.value == true) {
            repository.updateItem(series)
        }
    }

    private fun checkFormValidity() {
        _hasChanges.value = true
        _formValidity.value =
            _titleValidity.value == FormValidationState.VALID &&
            yearValidity.value == FormValidationState.VALID &&
            _writerValidity.value == FormValidationState.VALID &&
            _plotValidity.value == FormValidationState.VALID
    }
}