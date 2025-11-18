package com.thorapps.repaircars.repository

import com.thorapps.repaircars.network.ApiService
import com.thorapps.repaircars.network.models.ApiConversation
import com.thorapps.repaircars.network.models.ApiMessage
import com.thorapps.repaircars.network.models.CreateConversationRequest
import com.thorapps.repaircars.network.models.SendMessageRequest
import retrofit2.Response
import javax.inject.Inject

class ConversationRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getConversations(): Response<List<ApiConversation>> {
        return apiService.getConversations()
    }

    suspend fun createConversation(participants: List<String>): Response<ApiConversation> {
        return apiService.createConversation(CreateConversationRequest(participants))
    }

    suspend fun getConversationById(id: String): Response<ApiConversation> {
        return apiService.getConversationById(id)
    }

    suspend fun sendMessage(conversationId: String, message: SendMessageRequest): Response<ApiMessage> {
        return apiService.sendMessage(conversationId, message)
    }
}