package com.thorapps.repaircars.network

import com.thorapps.repaircars.network.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Contatos
    @GET("contacts")
    suspend fun getContacts(): Response<List<ApiContact>>

    @POST("contacts")
    suspend fun createContact(@Body contact: CreateContactRequest): Response<ApiContact>

    @GET("contacts/{id}")
    suspend fun getContactById(@Path("id") id: String): Response<ApiContact>

    // Notícias
    @GET("news")
    suspend fun getNews(
        @Query("categoria") categoria: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<NewsResponse>

    @GET("news/destaques")
    suspend fun getDestaques(): Response<List<ApiNews>>

    @GET("news/categorias")
    suspend fun getCategorias(): Response<List<String>>

    @GET("news/{id}")
    suspend fun getNewsById(@Path("id") id: String): Response<ApiNews>

    @GET("news/search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("page") page: Int = 1
    ): Response<NewsResponse>

    @POST("news")
    suspend fun createNews(@Body news: CreateNewsRequest): Response<ApiNews>

    // ✅ CONVERSATIONS - IMPLEMENTADOS
    @GET("conversations")
    suspend fun getConversations(): Response<List<ApiConversation>>

    @POST("conversations")
    suspend fun createConversation(@Body conversation: CreateConversationRequest): Response<ApiConversation>

    @GET("conversations/{id}")
    suspend fun getConversationById(@Path("id") id: String): Response<ApiConversation>

    @POST("conversations/{id}/messages")
    suspend fun sendMessage(@Path("id") conversationId: String, @Body message: SendMessageRequest): Response<ApiMessage>

    // ✅ PAYMENTS - IMPLEMENTADOS
    @GET("payments")
    suspend fun getPayments(): Response<List<ApiPayment>>

    @POST("payments")
    suspend fun createPayment(@Body payment: CreatePaymentRequest): Response<ApiPayment>

    @GET("payments/{id}")
    suspend fun getPaymentById(@Path("id") id: String): Response<ApiPayment>

    // ✅ LOCATIONS - IMPLEMENTADOS
    @GET("locations")
    suspend fun getLocations(): Response<List<ApiLocation>>

    @POST("locations")
    suspend fun createLocation(@Body location: CreateLocationRequest): Response<ApiLocation>

    @GET("locations/{id}")
    suspend fun getLocationById(@Path("id") id: String): Response<ApiLocation>
}