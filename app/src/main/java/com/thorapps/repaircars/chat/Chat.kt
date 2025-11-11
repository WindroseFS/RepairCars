package com.thorapps.repaircars.chat

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
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return format.format(date)
    }
}