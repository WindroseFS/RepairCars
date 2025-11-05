package com.thorapps.repaircars.chats

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val contactId: String,
    val contactName: String,
    val contactPhone: String,
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int = 0
) : Parcelable