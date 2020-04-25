package com.wawrzacz.entertainmentassistant.data.model

class Person (
    var id: Long? = null,
    val firstName: String,
    val middleName: String?,
    val lastName: String) {

    override fun toString(): String {
        return "$firstName $middleName $lastName"
    }
}