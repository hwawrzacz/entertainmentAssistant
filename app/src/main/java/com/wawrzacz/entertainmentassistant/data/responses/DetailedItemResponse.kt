package com.wawrzacz.entertainmentassistant.data.responses

import com.wawrzacz.entertainmentassistant.data.errors.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem

class DetailedItemResponse(
    var item: DetailedItem? = null,
    var response: ResponseStatus = ResponseStatus.NOT_INITIALIZED
) {
    override fun toString(): String {
        return "${item?.title} ${response.value}"
    }
}