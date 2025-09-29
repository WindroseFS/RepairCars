package com.thorapps.repaircars.database
data class Message(
    val id: Long = 0,
    val contactId: Long = 0,
    val text: String,
    val sender: String,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double? = null,
    val longitude: Double? = null
)
