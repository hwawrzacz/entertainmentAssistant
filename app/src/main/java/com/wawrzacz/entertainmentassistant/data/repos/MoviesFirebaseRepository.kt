package com.wawrzacz.entertainmentassistant.data.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem

object MoviesFirebaseRepository {
    private val _result = MutableLiveData<List<UniversalItem>>()
    val result: LiveData<List<UniversalItem>> = _result
}