package com.wawrzacz.entertainmentassistant.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.wawrzacz.entertainmentassistant.data.responses.CommonItemsListApiResponse

object BrowseRepository {
    private val apiRepository = ApiRepository
    private val firebaseRepository = MoviesFirebaseRepository

    private val _foundItemsResponse = MutableLiveData<CommonItemsListApiResponse?>()
    val foundItemsResponse: LiveData<CommonItemsListApiResponse?> = _foundItemsResponse

    fun findItems(query: String): LiveData<CommonItemsListApiResponse> {
        return MediatorLiveData<CommonItemsListApiResponse>().apply {
            val apiResult: CommonItemsListApiResponse? = null
            val firebaseResult: CommonItemsListApiResponse? = null

            fun updateData() {
                if (apiResult != null && firebaseResult != null) {
                    if (apiResult.response)
                    if (apiResult.items.isNotEmpty()) {}
                }
            }

        }
    }
}