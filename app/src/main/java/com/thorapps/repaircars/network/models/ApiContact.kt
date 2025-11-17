package com.thorapps.repaircars.network.models

import com.google.gson.annotations.SerializedName

data class ApiContact(
    @SerializedName("_id") val id: String,
    val name: String,
    val phone: String?,
    val email: String,
    val role: String?,
    val notes: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class CreateContactRequest(
    val name: String,
    val phone: String?,
    val email: String,
    val role: String? = "Cliente",
    val notes: String? = null
)