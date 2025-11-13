package com.thorapps.repaircars.database.models

data class MessageData(
    val contactId: String,
    val text: String,
    val isFromMe: Boolean
)
