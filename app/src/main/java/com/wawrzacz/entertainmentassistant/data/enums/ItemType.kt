package com.wawrzacz.entertainmentassistant.data.enums

import com.google.gson.annotations.SerializedName

enum class ItemType (val value: String) {
    @SerializedName("movie")
    MOVIE("movie"),
    @SerializedName("series")
    SERIES("series"),
    @SerializedName("game")
    GAME("game")
}