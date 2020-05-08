package com.wawrzacz.entertainmentassistant.data.responses

import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus
import com.wawrzacz.entertainmentassistant.data.model.DetailedItem

class DetailedItemResponse(
    var item: DetailedItem? = null,
    var responseStatus: ResponseStatus = ResponseStatus.NOT_INITIALIZED
) {
    override fun toString(): String {
        return "${item?.title} ${responseStatus.value}"
    }
}