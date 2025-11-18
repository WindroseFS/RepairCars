package com.thorapps.repaircars.network.models

import com.google.gson.annotations.SerializedName

data class ApiLocation(
    @SerializedName("_id") val id: String,
    val contact: ApiContact,
    val lat: Double,
    val lng: Double,
    val address: String?,
    val label: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class CreateLocationRequest(
    val contact: String,
    val lat: Double,
    val lng: Double,
    val address: String? = null,
    val label: String? = null
)