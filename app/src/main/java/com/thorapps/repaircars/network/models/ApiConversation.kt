package com.thorapps.repaircars.network.models

import com.google.gson.annotations.SerializedName

data class ApiConversation(
    @SerializedName("_id") val id: String,
    val participants: List<ApiContact>,
    val messages: List<ApiMessage>,
    val lastMessage: ApiMessage?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class ApiMessage(
    @SerializedName("_id") val id: String,
    val sender: ApiContact,
    val text: String,
    val timestamp: String,
    val latitude: Double?,
    val longitude: Double?
)

data class CreateConversationRequest(
    val participants: List<String>
)

// ✅ CORREÇÃO: SendMessageRequest movido para cá
data class SendMessageRequest(
    val sender: String,
    val text: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)