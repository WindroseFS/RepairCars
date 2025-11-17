package com.thorapps.repaircars.repository

import com.thorapps.repaircars.network.ApiClient
import com.thorapps.repaircars.network.models.ApiContact
import com.thorapps.repaircars.network.models.CreateContactRequest

class ContactRepository {
    private val apiService = ApiClient.apiService

    suspend fun getContactsFromApi(): List<ApiContact> {
        return try {
            val response = apiService.getContacts()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createContactOnApi(contact: CreateContactRequest): Boolean {
        return try {
            val response = apiService.createContact(contact)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}