package com.wawrzacz.entertainmentassistant.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.wawrzacz.entertainmentassistant.data.enums.ItemType
import com.wawrzacz.entertainmentassistant.data.enums.WatchableSection
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.responses.DetailedItemResponse

object DetailsRepository {

    private val moviesFirebaseRepository = MoviesFirebaseRepository
    private val seriesFirebaseRepository = SeriesFirebaseRepository
//    private val firebaseRepository = GamesFirebaseRepository
    private val apiRepository = ApiRepository

    fun isMovieInSection(id: String?, section: WatchableSection): LiveData<Boolean> {
        return moviesFirebaseRepository.getMovieSectionValue(id, section)
    }

    fun isSeriesInSection(id: String?, section: WatchableSection): LiveData<Boolean> {
        return seriesFirebaseRepository.getSeriesSectionValue(id, section)
    }

    fun getDetailedMovie(id: String): LiveData<DetailedItemResponse> {
        return MediatorLiveData<DetailedItemResponse>().apply {
            var firebaseItemResult = DetailedItemResponse()
            var apiItemResult = DetailedItemResponse()

            fun checkResults() {
                if (firebaseItemResult.responseStatus == ResponseStatus.SUCCESS)
                    this.value = firebaseItemResult
                else if (firebaseItemResult.responseStatus != ResponseStatus.NOT_INITIALIZED && apiItemResult.responseStatus != ResponseStatus.NOT_INITIALIZED)
                    this.value = apiItemResult

            }

            addSource(moviesFirebaseRepository.getSingleItem(id)) {
                firebaseItemResult = it ?: DetailedItemResponse(null, ResponseStatus.NO_RESULT)
                checkResults()
            }

            addSource(apiRepository.getItem(id)) {
                apiItemResult = it ?: DetailedItemResponse(null, ResponseStatus.NO_RESULT)
                checkResults()
            }
        }
    }

    fun getDetailedSeries(id: String): LiveData<DetailedItemResponse> {
        return MediatorLiveData<DetailedItemResponse>().apply {
            var firebaseItemResult = DetailedItemResponse()
            var apiItemResult = DetailedItemResponse()

            fun checkResults() {
                if (firebaseItemResult.responseStatus == ResponseStatus.SUCCESS)
                    this.value = firebaseItemResult
                else if (firebaseItemResult.responseStatus != ResponseStatus.NOT_INITIALIZED && apiItemResult.responseStatus != ResponseStatus.NOT_INITIALIZED)
                    this.value = apiItemResult

            }

            addSource(seriesFirebaseRepository.getSingleItem(id)) {
                firebaseItemResult = it ?: DetailedItemResponse(null, ResponseStatus.NO_RESULT)
                checkResults()
            }

            addSource(apiRepository.getItem(id)) {
                apiItemResult = it ?: DetailedItemResponse(null, ResponseStatus.NO_RESULT)
                checkResults()
            }
        }
    }

    fun toggleMovieSection(section: WatchableSection, item: DetailedItem) {
        moviesFirebaseRepository.toggleItemSection(section, item)
    }
    fun toggleSeriesSection(section: WatchableSection, item: DetailedItem) {
        seriesFirebaseRepository.toggleItemSection(section, item)
    }
//    fun toggleGameSection(section: WatchableSection, item: DetailedItem) {
//        gamesFirebaseRepository.toggleItemSection(section, item)
//    }
    
}