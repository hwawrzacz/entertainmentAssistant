package com.wawrzacz.entertainmentassistant.data.model

import android.icu.text.CaseMap
import com.google.gson.annotations.SerializedName

class UniversalItem(
    @SerializedName("imdbID")
    val id: String,
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Poster")
    val posterURL: String,
    val isFavourite: Boolean
) {
    override fun toString(): String {
        return "$title $year"
    }
}