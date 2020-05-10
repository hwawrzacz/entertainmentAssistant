package com.wawrzacz.entertainmentassistant.data.model

import com.google.gson.annotations.SerializedName
import com.wawrzacz.entertainmentassistant.data.enums.ItemSource
import com.wawrzacz.entertainmentassistant.data.enums.ItemType

class DetailedItem(
    @SerializedName("imdbID")
    var id: String = "N/A",
    @SerializedName("Title")
    var title: String = "N/A",
    @SerializedName("Production")
    var production: String = "N/A",
    @SerializedName("Type")
    var type: ItemType = ItemType.MOVIE,
    @SerializedName("Year")
    var year: String = "N/A",
    @SerializedName("Runtime")
    var duration: String = "N/A",
    @SerializedName("totalSeasons")
    var totalSeasons: String? = "N/A",
    @SerializedName("Genre")
    var genre: String = "N/A",
    @SerializedName("Poster")
    var posterUrl: String = "N/A",
    @SerializedName("Director")
    var director: String = "N/A",
    @SerializedName("Writer")
    var writer: String = "N/A",
    @SerializedName("Plot")
    var plot: String = "N/A",
    var queryTitle: String = "N/A",
    var source: ItemSource = ItemSource.API
    ){

    init {
        queryTitle = title.toLowerCase()
    }

    override fun toString(): String {
        return "$title $type $year"
    }
}