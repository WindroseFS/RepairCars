package com.thorapps.repaircars.database.models

data class MessageWithOptions(
    val message: Message,
    val options: List<String>
)
