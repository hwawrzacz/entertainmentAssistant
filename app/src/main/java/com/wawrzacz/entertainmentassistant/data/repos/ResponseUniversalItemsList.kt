package com.wawrzacz.entertainmentassistant.data.repos

import com.google.gson.annotations.SerializedName
import com.wawrzacz.entertainmentassistant.data.model.UniversalItem

class ResponseUniversalItemsList (
    @SerializedName("Search")
    val items: List<UniversalItem>,
    @SerializedName("totalResults")
    val totalResults: String,
    @SerializedName("Response")
    val response: Boolean
){
    override fun toString(): String {
        return "${items[0]}"
    }
}