package com.wawrzacz.entertainmentassistant.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.responses.DetailedItemResponse

object DetailedMovieRepository {

    private val firebaseRepository = MoviesFirebaseRepository
    private val apiRepository = ApiRepository

    fun isMovieInSection(id: String?, section: WatchableSection): LiveData<Boolean> {
        return firebaseRepository.getMovieSectionValue(id, section)
    }

    fun getDetailedItem(id: String): LiveData<DetailedItemResponse> {
        return MediatorLiveData<DetailedItemResponse>().apply {
            var firebaseItemResult = DetailedItemResponse()
            var apiItemResult = DetailedItemResponse()

            fun checkResults() {
                if (firebaseItemResult.responseStatus == ResponseStatus.SUCCESS) {
                    this.value = firebaseItemResult
                }
                else if (firebaseItemResult.responseStatus != ResponseStatus.NOT_INITIALIZED && apiItemResult.responseStatus != ResponseStatus.NOT_INITIALIZED) {
                    this.value = apiItemResult
                }
            }

            addSource(firebaseRepository.getSingleItem(id)) {
                firebaseItemResult = it ?: DetailedItemResponse(null, ResponseStatus.NO_RESULT)
                checkResults()
            }

            addSource(apiRepository.getItem(id)) {
                apiItemResult = it ?: DetailedItemResponse(null, ResponseStatus.NO_RESULT)
                checkResults()
            }
        }
    }

    fun toggleItemSection(section: WatchableSection, movie: DetailedItem) {
        Log.i("schab", "add to $section")
        firebaseRepository.toggleItemSection(section, movie)
    }
    
}