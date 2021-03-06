package com.wawrzacz.entertainmentassistant.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.responses.CommonItemsListApiResponse
import com.wawrzacz.entertainmentassistant.data.responses.DetailedItemResponse
import com.wawrzacz.entertainmentassistant.data.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiRepository {
    private val retrofit: Retrofit
    private val service: ApiService
    private const val BASE_URL = "http://www.omdbapi.com" //Resources.getSystem().getString(R.string.retrofit_base_url)
    private const val API_KEY = "373cac09" //Resources.getSystem().getString(R.string.retrofit_api_key)

    private val _foundItemsResponse = MutableLiveData<CommonItemsListApiResponse?>()
    val foundItemsResponse: LiveData<CommonItemsListApiResponse?> = _foundItemsResponse

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

    fun getItem(id: String): LiveData<DetailedItemResponse?> {

        val result = MutableLiveData<DetailedItemResponse?>()

        service.getMovieById(id, API_KEY).enqueue(object: Callback<DetailedItem?>{
            override fun onResponse(call: Call<DetailedItem?>, response: Response<DetailedItem?>) {
                if (response.isSuccessful) {
                    result.value = DetailedItemResponse(response.body(), ResponseStatus.SUCCESS)
                }
                else{
                    result.value = DetailedItemResponse(null, ResponseStatus.ERROR)
                }
            }
            override fun onFailure(call: Call<DetailedItem?>, t: Throwable) {
                result.value = DetailedItemResponse(null, ResponseStatus.ERROR)
            }
        })
        return result
    }

    fun findItems(query: String) {
        service.findItems(query, API_KEY)
            .enqueue(object: Callback<CommonItemsListApiResponse> {
                override fun onResponse(call: Call<CommonItemsListApiResponse>, response: Response<CommonItemsListApiResponse>) {
                    if (response.isSuccessful){
                        val apiResult = response.body()
                        if (apiResult?.totalResults == null || apiResult?.items == null) {
                            _foundItemsResponse.value = CommonItemsListApiResponse(items = null, response = false)
                        } else {
                            _foundItemsResponse.value = CommonItemsListApiResponse(apiResult.items, apiResult.totalResults, true)
                        }
                    }
                    else {
                        _foundItemsResponse.value = CommonItemsListApiResponse(items = null, response = false)
                    }
                }
                override fun onFailure(call: Call<CommonItemsListApiResponse>, t: Throwable) {
                    _foundItemsResponse.value = CommonItemsListApiResponse(items = null, response = false)
                }
            })
    }
}