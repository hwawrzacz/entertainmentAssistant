package com.wawrzacz.entertainmentassistant.data.model

import com.google.gson.annotations.SerializedName

class DetailedItem(
    @SerializedName("imdbID")
    val id: String = "",
    @SerializedName("Title")
    val title: String = "",
    @SerializedName("Production")
    val production: String = "",
    @SerializedName("Type")
    val type: String = "",
    @SerializedName("Year")
    val year: String = "",
    @SerializedName("Runtime")
    val runtime: String = "",
    @SerializedName("totalSeasons")
    val totalSeasons: String? = "",
    @SerializedName("Genre")
    val genre: String = "",
    @SerializedName("Poster")
    val posterUrl: String = "",
    @SerializedName("Director")
    val director: String = "",
    @SerializedName("Writer")
    val writer: String = "",
    @SerializedName("Plot")
    val plot: String = "",
    @SerializedName("Response")
    val response: String = "false"
){

    override fun toString(): String {
        return "$title $type $year"
    }
}