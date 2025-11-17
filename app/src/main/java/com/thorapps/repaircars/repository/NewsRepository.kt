package com.thorapps.repaircars.repository

import com.thorapps.repaircars.network.ApiClient
import com.thorapps.repaircars.network.models.ApiNews
import com.thorapps.repaircars.network.models.CreateNewsRequest
import com.thorapps.repaircars.network.models.NewsResponse

class NewsRepository {
    private val apiService = ApiClient.apiService

    suspend fun getNews(
        categoria: String? = null,
        page: Int = 1,
        limit: Int = 10
    ): NewsResponse? {
        return try {
            val response = apiService.getNews(categoria, page, limit)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getDestaques(): List<ApiNews> {
        return try {
            val response = apiService.getDestaques()
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCategorias(): List<String> {
        return try {
            val response = apiService.getCategorias()
            if (response.isSuccessful) {
                response.body() ?: listOf("Todas")
            } else {
                listOf("Todas")
            }
        } catch (e: Exception) {
            listOf("Todas")
        }
    }

    suspend fun getNewsById(id: String): ApiNews? {
        return try {
            val response = apiService.getNewsById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchNews(query: String, page: Int = 1): NewsResponse? {
        return try {
            val response = apiService.searchNews(query, page)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createNews(news: CreateNewsRequest): ApiNews? {
        return try {
            val response = apiService.createNews(news)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}