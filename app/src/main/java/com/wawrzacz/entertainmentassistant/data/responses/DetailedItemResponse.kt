package com.wawrzacz.entertainmentassistant.data.responses

import com.wawrzacz.entertainmentassistant.data.errors.Response
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem

class DetailedItemResponse(
    var item: DetailedItem? = null,
    var response: Response = Response.NOT_INITIALIZED
) {
    override fun toString(): String {
        return "${item?.title} ${response.value}"
    }
}