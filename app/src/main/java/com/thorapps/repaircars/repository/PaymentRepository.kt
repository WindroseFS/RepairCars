package com.thorapps.repaircars.repository

import com.thorapps.repaircars.network.ApiService
import com.thorapps.repaircars.network.models.ApiPayment
import com.thorapps.repaircars.network.models.CreatePaymentRequest
import retrofit2.Response
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getPayments(): Response<List<ApiPayment>> {
        return apiService.getPayments()
    }

    suspend fun createPayment(payment: CreatePaymentRequest): Response<ApiPayment> {
        return apiService.createPayment(payment)
    }

    suspend fun getPaymentById(id: String): Response<ApiPayment> {
        return apiService.getPaymentById(id)
    }
}