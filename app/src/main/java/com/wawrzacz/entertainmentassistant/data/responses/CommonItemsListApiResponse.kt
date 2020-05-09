package com.wawrzacz.entertainmentassistant.data.responses

import com.google.gson.annotations.SerializedName
import com.wawrzacz.entertainmentassistant.data.model.CommonListItem

class CommonItemsListApiResponse (
    @SerializedName("Search")
    val items: ArrayList<CommonListItem>? = null,
    @SerializedName("totalResults")
    val totalResults: String? = "0",
    @SerializedName("Response")
    val response: Boolean = false
){
    override fun toString(): String {
        return "${items?.get(0)}"
    }
}