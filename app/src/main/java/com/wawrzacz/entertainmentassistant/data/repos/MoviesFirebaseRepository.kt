package com.wawrzacz.entertainmentassistant.data.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wawrzacz.entertainmentassistant.data.model.Movie
import com.wawrzacz.entertainmentassistant.data.model.MovieSimple

object MoviesFirebaseRepository {
    private val _result = MutableLiveData<List<MovieSimple>>()
    val result: LiveData<List<MovieSimple>> = _result
}