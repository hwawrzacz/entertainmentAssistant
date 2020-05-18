package com.wawrzacz.entertainmentassistant.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.responses.CommonItemsListResponse

object BrowseRepository {
    private val apiRepository = ApiRepository
    private val firebaseRepository = GeneralFirebaseRepository

    fun findItems(query: String?): LiveData<CommonItemsListResponse> {
        return MediatorLiveData<CommonItemsListResponse>().apply {
            var apiResult = CommonItemsListResponse(null, ResponseStatus.NOT_INITIALIZED)
            var firebaseResult = CommonItemsListResponse(null, ResponseStatus.NOT_INITIALIZED)

            fun checkData() {
                if (firebaseResult.responseStatus != ResponseStatus.NOT_INITIALIZED &&
                    apiResult.responseStatus != ResponseStatus.NOT_INITIALIZED) {

                    if (firebaseResult.responseStatus == ResponseStatus.NO_RESULT &&
                        apiResult.responseStatus == ResponseStatus.NO_RESULT){
                        this.value = CommonItemsListResponse(null, ResponseStatus.NO_RESULT)
                    }
                    else if (firebaseResult.responseStatus == ResponseStatus.ERROR &&
                        apiResult.responseStatus == ResponseStatus.ERROR) {
                        this.value = CommonItemsListResponse(null, ResponseStatus.ERROR)
                    } else {
                        val mergedMovies = mergeResults(firebaseResult.items, apiResult.items)

                        if (mergedMovies.isNullOrEmpty()) {
                            this.value = CommonItemsListResponse(null, ResponseStatus.NO_RESULT)
                        }
                        else {
                            this.value = CommonItemsListResponse(mergedMovies, ResponseStatus.SUCCESS)
                        }
                    }
                }
            }

            if (query.isNullOrBlank()) {
                // To display default message, when query is cleared
                this.value = CommonItemsListResponse(null, ResponseStatus.NOT_INITIALIZED)
            } else {
                addSource(firebaseRepository.foundAllMovies) {
                    firebaseResult = it
                    checkData()
                }

                addSource(apiRepository.foundItemsResponse) {
                    apiResult = when {
                        it == null -> CommonItemsListResponse(null, ResponseStatus.NOT_INITIALIZED)
                        !it.response -> {
                            CommonItemsListResponse(null, ResponseStatus.NO_RESULT)
                        }
                        else -> CommonItemsListResponse(it.items, ResponseStatus.SUCCESS)
                    }
                    checkData()
                }

                firebaseRepository.findAllItemsByTitle(query)
                apiRepository.findItems(query)
            }
        }
    }

    private fun mergeResults(
        firebaseResults: List<CommonListItem>?,
        apiResults: List<CommonListItem>?
    ): List<CommonListItem> {
        val mergedResults = arrayListOf<CommonListItem>()

        firebaseResults?.forEach { mergedResults.add(it) }
        apiResults?.forEach {
            if (!contains(mergedResults, it)) mergedResults.add(it)
        }
        return mergedResults
    }

    private fun contains(list: List<CommonListItem>, item: CommonListItem): Boolean {
        var counter = 0
        list.forEach { if (it.equals(item)) counter++ }
        return counter > 0
    }
}