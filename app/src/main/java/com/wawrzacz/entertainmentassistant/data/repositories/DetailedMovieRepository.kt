package com.wawrzacz.entertainmentassistant.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.data.errors.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.responses.DetailedItemResponse

object DetailedMovieRepository {

    private val firebaseRepository = MoviesFirebaseRepository
    private val apiRepository = ApiRepository

    fun isMovieInSection(id: String?, section: WatchableSection): LiveData<Boolean> {
        return firebaseRepository.getMovieSectionValue(id, section)
    }

    fun getDetailedItem(id: String): LiveData<DetailedItem?> {
        return MediatorLiveData<DetailedItem?>().apply {
            val firebaseItemResult = DetailedItemResponse()
            val apiItemResult = DetailedItemResponse()

            fun checkResults() {
                if (firebaseItemResult.response == ResponseStatus.SUCCESS) {
                    Log.i("schab", "itemSource: Firebase")
                    this.value = firebaseItemResult.item
                }
                else if (firebaseItemResult.response != ResponseStatus.NOT_INITIALIZED && apiItemResult.response != ResponseStatus.NOT_INITIALIZED) {
                    Log.i("schab", "itemSource: API")
                    this.value = apiItemResult.item
                }
            }

            addSource(firebaseRepository.getSingleMovie(id)) {
                if (it == null) firebaseItemResult.response = ResponseStatus.NO_RESULT
                else firebaseItemResult.response = ResponseStatus.SUCCESS

                firebaseItemResult.item = it
                checkResults()
            }

            addSource(apiRepository.getItem(id)) {
                if (it == null) apiItemResult.response = ResponseStatus.NO_RESULT
                else apiItemResult.response = ResponseStatus.SUCCESS

                apiItemResult.item = it
                checkResults()
            }
        }
    }

    fun toggleItemSection(section: WatchableSection, movie: DetailedItem) {
        Log.i("schab", "add to $section")
        firebaseRepository.toggleItemSection(section, movie)
    }
    
}