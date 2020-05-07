package com.wawrzacz.entertainmentassistant.data.enums

enum class RegexPattern(val value: String) {
    LIST_OF_FULL_NAMES("^([A-z]+((, )?|( )?))+\$"),
    LIST_OF_GENRES("^([A-z]+((, )?|( )?))+\$")
}