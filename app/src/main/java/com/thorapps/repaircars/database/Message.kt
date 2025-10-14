package com.thorapps.repaircars.database

data class Message(
    val id: Long,
    val contactId: Long,
    val text: String,
    val sender: String,
    val timestamp: Long,
    val latitude: Double?,
    val longitude: Double?
)