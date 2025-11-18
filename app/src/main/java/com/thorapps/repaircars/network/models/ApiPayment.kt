package com.thorapps.repaircars.network.models

import com.google.gson.annotations.SerializedName

data class ApiPayment(
    @SerializedName("_id") val id: String,
    val contact: ApiContact,
    val amount: Double,
    val currency: String,
    val method: String?,
    val status: String,
    val notes: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class CreatePaymentRequest(
    val contact: String,
    val amount: Double,
    val currency: String = "BRL",
    val method: String? = null,
    val status: String = "pending",
    val notes: String? = null
)