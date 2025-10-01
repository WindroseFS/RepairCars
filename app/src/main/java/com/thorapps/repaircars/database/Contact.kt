package com.thorapps.repaircars.database

data class Contact(
    val id: Long,
    val name: String,
    val email: String,
    val lastMessage: String? = null,
    val lastMessageTimestamp: Long = 0L,
    val unreadCount: Int = 0
)