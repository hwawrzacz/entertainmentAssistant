package com.wawrzacz.entertainmentassistant.data.model

import com.google.gson.annotations.SerializedName

class Movie (
    @SerializedName("imdbID")
    var id: String = "N/A",
    @SerializedName("Title")
    var title: String = "N/A",
    @SerializedName("Production")
    var production: String = "N/A",
    @SerializedName("Poster")
    var posterUrl: String = "N/A",
    @SerializedName("Runtime")
    var duration: String = "N/A",
    @SerializedName("Director")
    var director: String = "N/A",
    @SerializedName("Country")
    var country: String = "N/A",
    @SerializedName("Year")
    var year: String = "N/A",
    @SerializedName("Plot")
    var plot: String = "N/A",
    @SerializedName("Genre")
    var genre: String = "N/A",
    val detailedItem: DetailedItem? = null
){
    init {
        if (detailedItem != null) {
            id = detailedItem.id
            title = detailedItem.title
            posterUrl = detailedItem.posterUrl
            duration = detailedItem.duration
            director = detailedItem.director
            year = detailedItem.year
            genre = detailedItem.genre
            plot = detailedItem.plot
        }
    }
    override fun toString(): String {
        return "$title by $director | $country $year $duration min"
    }
}