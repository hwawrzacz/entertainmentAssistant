package com.wawrzacz.entertainmentassistant.activity_main.movies.movie_creation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wawrzacz.entertainmentassistant.data.errors.FormValidationState
import com.wawrzacz.entertainmentassistant.data.errors.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.model.Movie
import com.wawrzacz.entertainmentassistant.data.repositories.MoviesFirebaseRepository

class MovieCreationViewModel: ViewModel() {

    private val repository = MoviesFirebaseRepository

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

    private val movie = Movie()

    val movieCreationStatus: LiveData<ResponseStatus> = repository.movieCreationResult

    fun validateTitle(value: String) {
        if (value.isBlank())
            _titleValidity.value = FormValidationState.FIELD_EMPTY
        else {
            _titleValidity.value = FormValidationState.VALID
            movie.title = value
        }
        checkFormValidity()
    }

    fun validateYear(value: String?) {
        if (value.isNullOrBlank())
            _yearValidity.value = FormValidationState.FIELD_EMPTY
        else {
            val numberValue = value.toInt()
            if (numberValue < 1895)
                _yearValidity.value = FormValidationState.YEAR_BEFORE_CINEMATOGRAPHY
            else {
                _yearValidity.value = FormValidationState.VALID
                movie.year = value
            }
        }
        checkFormValidity()
    }

    fun validateDirector(value: String) {
        if (value.isBlank())
            _directorValidity.value = FormValidationState.FIELD_EMPTY
        else {
            _directorValidity.value = FormValidationState.VALID
            movie.director = value
        }
        checkFormValidity()
    }

    fun validatePlot(value: String) {
        if (value.isBlank())
            _plotValidity.value = FormValidationState.FIELD_EMPTY
        else {
            _plotValidity.value = FormValidationState.VALID
            movie.plot = value
        }
        checkFormValidity()
    }

    fun setProduction(value: String) {
        movie.production = value
    }

    fun setDuration(value: String) {
        movie.duration = value
    }

    fun setGenre(value: String) {
        movie.genre = value
    }

    fun createMovie() {
        if (_formValidity.value == true) {
            repository.createMovie(movie)
        }
    }

    private fun checkFormValidity() {
        _formValidity.value =
            _titleValidity.value == FormValidationState.VALID &&
            yearValidity.value == FormValidationState.VALID &&
            _directorValidity.value == FormValidationState.VALID &&
            _plotValidity.value == FormValidationState.VALID
    }
}