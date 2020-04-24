package com.wawrzacz.entertainmentassistant.data

import java.time.Year

class Movie (
    var id: Long?,
    val title: String,
    val image: String,
    val duration: Int,
    val director: String,
    val country: String,
    val year: Int,
    val isFavourite: Boolean
){
    override fun toString(): String {
        return "$title by $director | $country $year $duration min"
    }
}