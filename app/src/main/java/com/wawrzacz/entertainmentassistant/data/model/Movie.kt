package com.wawrzacz.entertainmentassistant.data.model

import com.google.gson.annotations.SerializedName
import java.time.Year

class Movie (
    @SerializedName("imdbID")
    var id: Long?,
    @SerializedName("Title")
    val title: String,
    @SerializedName("Poster")
    val image: String,
    @SerializedName("Runtime")
    val duration: String,
    @SerializedName("Director")
    val director: String,
    @SerializedName("Country")
    val country: String,
    @SerializedName("Year")
    val year: Int,
    val isFavourite: Boolean
){
    override fun toString(): String {
        return "$title by $director | $country $year $duration min"
    }
}