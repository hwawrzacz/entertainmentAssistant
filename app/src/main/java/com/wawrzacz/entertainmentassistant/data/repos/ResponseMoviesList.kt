package com.wawrzacz.entertainmentassistant.data.repos

import com.google.gson.annotations.SerializedName
import com.wawrzacz.entertainmentassistant.data.model.MovieSimple

class ResponseMoviesList (
    @SerializedName("Search")
    val movies: List<MovieSimple>,
    @SerializedName("totalResults")
    val totalResults: String,
    @SerializedName("Response")
    val response: Boolean
){
    override fun toString(): String {
        return "${movies[0]}"
    }
}