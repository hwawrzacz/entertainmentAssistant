package com.wawrzacz.entertainmentassistant.data.retrofit

import com.wawrzacz.entertainmentassistant.data.model.DetailedItem
import com.wawrzacz.entertainmentassistant.data.repos.ResponseUniversalItemsList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/?")
    fun findItems(@Query("s") searchPhrase: String,
                  @Query("apikey") apikey: String): Call<ResponseUniversalItemsList>
    @GET("/")
    fun getMovieById(@Query("i") searchPhrase: String?,
                   @Query("apikey") apikey: String): Call<DetailedItem>
}