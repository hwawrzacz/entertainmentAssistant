package com.wawrzacz.entertainmentassistant.data.model

import android.icu.text.CaseMap
import com.google.gson.annotations.SerializedName

class CommonListItem(
    @SerializedName("imdbID")
    val id: String = "",
    @SerializedName("Title")
    val title: String = "",
    @SerializedName("Year")
    val year: String = "",
    @SerializedName("Type")
    val type: String = "",
    @SerializedName("Poster")
    val posterUrl: String = ""
) {
    override fun toString(): String {
        return "$title $year"
    }

    fun equals(item: CommonListItem): Boolean {
        return item.id == id ||
                item.title == title &&
                item.year == year &&
                item.type == type &&
                item.posterUrl == posterUrl
    }
}