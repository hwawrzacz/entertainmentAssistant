package com.wawrzacz.entertainmentassistant.data.responses

import com.google.gson.annotations.SerializedName
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem

class CommonItemsListApiResponse (
    @SerializedName("Search")
    val items: List<CommonListItem>,
    @SerializedName("totalResults")
    val totalResults: String,
    @SerializedName("Response")
    val response: Boolean,
    val isSuccessful: Boolean
){
    override fun toString(): String {
        return "${items[0]}"
    }
}