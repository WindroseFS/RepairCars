package com.thorapps.repaircars.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageData(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val sender: String,
    val content: String,
    val timestamp: String,
    val messageType: String,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val isRead: Boolean
)