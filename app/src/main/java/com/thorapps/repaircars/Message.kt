package com.thorapps.repaircars

data class Contact(
    val id: Long,
    val name: String
)

data class ContactDisplay(
    val id: Long,
    val name: String,
    val lastMessage: String
)

data class Message(
    val id: Long = 0,
    val contactId: Long = 0,
    val text: String,
    val sender: String,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double? = null,
    val longitude: Double? = null
)
