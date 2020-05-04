package com.wawrzacz.entertainmentassistant.data.model

enum class SearchResult(val value: String) {
    NOT_INITIALIZED("NOT_INITIALIZED"),
    NO_RESULTS("NO_RESULTS"),
    ERROR_GETTING_DATA("ERROR_GETTING_DATA"),
    SUCCESS("SUCCESS")
}
