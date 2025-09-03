package com.thorapps.repaircars

data class Message(
    val text: String,
    val sender: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)
