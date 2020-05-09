package com.wawrzacz.entertainmentassistant.data.responses

import com.wawrzacz.entertainmentassistant.data.model.CommonListItem
import com.wawrzacz.entertainmentassistant.data.response_statuses.ResponseStatus

class CommonItemsListResponse(
    var items: List<CommonListItem>? = null,
    var responseStatus: ResponseStatus = ResponseStatus.NOT_INITIALIZED
) {
}