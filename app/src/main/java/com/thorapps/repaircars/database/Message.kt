package com.thorapps.repaircars.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val id: Long,
    val contactId: String,
    val text: String,
    val isSentByMe: Boolean,
    val timestamp: Long,
    val latitude: Double? = null,
    val longitude: Double? = null
): Parcelable {
    constructor(
        contactId: String,
        text: String,
        isSentByMe: Boolean,
        timestamp: Long = System.currentTimeMillis(),
        latitude: Double? = null,
        longitude: Double? = null
    ) : this(0, contactId, text, isSentByMe, timestamp, latitude, longitude)
}