package com.wawrzacz.entertainmentassistant.data.repos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.responses.ResponseUniversalItemsList
import com.wawrzacz.entertainmentassistant.data.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiRepository {
    private val retrofit: Retrofit
    private val service: ApiService
    private val BASE_URL = "http://www.omdbapi.com" //Resources.getSystem().getString(R.string.retrofit_base_url)
    private val API_KEY = "373cac09" //Resources.getSystem().getString(R.string.retrofit_api_key)

    private val _searchedItemsResponse = MutableLiveData<ResponseUniversalItemsList?>()
    val foundItemsResponse: LiveData<ResponseUniversalItemsList?> = _searchedItemsResponse

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()
            ))
            .build()

        service = retrofit.create(ApiService::class.java)
    }

    fun getItem(id: String): LiveData<DetailedItem> {
        val itemResult = MutableLiveData<DetailedItem>()

        service.getMovieById(id, API_KEY).enqueue(object: Callback<DetailedItem>{
            override fun onResponse(call: Call<DetailedItem>, response: Response<DetailedItem>) {
                if (response.isSuccessful) {
                    itemResult.value = response.body()
                } else {
                    itemResult.value = null
                }
            }

            override fun onFailure(call: Call<DetailedItem>, t: Throwable) {
                itemResult.value = null
            }
        })

        return itemResult
    }

    fun findItems(query: String) {
        service.findItems(query, API_KEY)
            .enqueue(object: Callback<ResponseUniversalItemsList> {
                override fun onResponse(call: Call<ResponseUniversalItemsList>, response: Response<ResponseUniversalItemsList>) {
                    if (response.isSuccessful) {
                        _searchedItemsResponse.value = response.body()
                    }
                    else {
                        _searchedItemsResponse.value = null
                    }
                }

                override fun onFailure(call: Call<ResponseUniversalItemsList>, t: Throwable) {
                    _searchedItemsResponse.value = null
                }
            })
    }
}