package com.wawrzacz.entertainmentassistant.data.model

import com.google.gson.annotations.SerializedName
import com.wawrzacz.entertainmentassistant.data.enums.ItemType

class CommonListItem(
    @SerializedName("imdbID")
    val id: String = "N/A",
    @SerializedName("Title")
    val title: String = "N/A",
    @SerializedName("Year")
    val year: String = "N/A",
    @SerializedName("Type")
    val type: ItemType = ItemType.MOVIE,
    @SerializedName("Poster")
    val posterUrl: String = "N/A"
) {
    override fun toString(): String {
        return "$title $year"
    }

    fun equals(item: CommonListItem): Boolean {
        return item.id == id ||
                (item.title == title &&
                item.year == year &&
                item.type == type &&
                item.posterUrl == posterUrl)
    }
}