package com.wawrzacz.entertainmentassistant.data.responses

import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus

class CommonItemsListResponse(
    var items: ArrayList<CommonListItem>? = null,
    var responseStatus: ResponseStatus = ResponseStatus.NOT_INITIALIZED
) {
}