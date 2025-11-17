package com.thorapps.repaircars.network

import com.thorapps.repaircars.network.models.ApiContact
import com.thorapps.repaircars.network.models.ApiNews
import com.thorapps.repaircars.network.models.CreateContactRequest
import com.thorapps.repaircars.network.models.NewsResponse
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
    suspend fun createNews(@Body news: CreateContactRequest): Response<ApiNews>
}

data class SendMessageRequest(
    val sender: String,
    val text: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)