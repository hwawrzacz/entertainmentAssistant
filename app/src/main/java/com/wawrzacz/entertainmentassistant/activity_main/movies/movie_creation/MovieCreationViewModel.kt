package com.wawrzacz.entertainmentassistant.activity_main.movies.movie_creation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.errors.FormValidationState

class MovieCreationViewModel: ViewModel() {

    //#region Validation fields
    private val _formValidity = MutableLiveData(false)
    val formValidity: LiveData<Boolean> = _formValidity

    private val _titleValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val titleValidity: LiveData<FormValidationState> = _titleValidity

    private val _yearValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val yearValidity: LiveData<FormValidationState> = _yearValidity

    private val _durationValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val durationValidity: LiveData<FormValidationState> = _durationValidity

    private val _directorValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val directorValidity: LiveData<FormValidationState> = _directorValidity

    private val _genreValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val genreValidity: LiveData<FormValidationState> = _genreValidity

    private val _plotValidity = MutableLiveData(FormValidationState.NOT_INITIALIZED)
    val plotValidity: LiveData<FormValidationState> = _plotValidity

    fun validateTitle(value: String) {
        Log.i("schab", "Title changed")
        if (value.isBlank())
            _titleValidity.value = FormValidationState.FIELD_EMPTY
        else
            _titleValidity.value = FormValidationState.VALID
        checkFormValidity()
    }

    fun validateYear(value: String?) {
        if (value.isNullOrBlank())
            _yearValidity.value = FormValidationState.FIELD_EMPTY
        else {
            val numberValue = value.toInt()
            if (numberValue < 1895)
                _yearValidity.value = FormValidationState.YEAR_BEFORE_CINEMATOGRAPHY
            else
                _yearValidity.value = FormValidationState.VALID
        }
        checkFormValidity()
    }

    fun validateDirector(value: String) {
        if (value.isBlank())
            _titleValidity.value = FormValidationState.FIELD_EMPTY
        else
            _directorValidity.value = FormValidationState.VALID
        checkFormValidity()
    }

    fun validatePlot(value: String) {
        if (value.isBlank())
            _titleValidity.value = FormValidationState.FIELD_EMPTY
        else
            _plotValidity.value = FormValidationState.VALID
        checkFormValidity()
    }

    private fun checkFormValidity() {
        _formValidity.value =
            _titleValidity.value == FormValidationState.VALID &&
            yearValidity.value == FormValidationState.VALID &&
            _directorValidity.value == FormValidationState.VALID &&
            _plotValidity.value == FormValidationState.VALID
    }
}