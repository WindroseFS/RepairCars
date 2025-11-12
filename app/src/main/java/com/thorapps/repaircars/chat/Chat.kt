package com.thorapps.repaircars.chat

import java.text.SimpleDateFormat
import java.util.*

data class Chat(
    val contactId: String,
    val contactName: String,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int = 0
) {
    fun getAvatarText(): String {
        return if (contactName.isNotEmpty()) contactName[0].uppercase() else "?"
    }

    fun getFormattedTime(): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }
}
