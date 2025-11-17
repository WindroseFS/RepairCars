package com.thorapps.repaircars.network.models

import com.google.gson.annotations.SerializedName

data class ApiNews(
    @SerializedName("_id") val id: String,
    val titulo: String,
    val descricao: String,
    val conteudo: String,
    val data: String,
    @SerializedName("dataPublicacao") val dataPublicacao: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    val categoria: String,
    val autor: String,
    val tags: List<String>,
    val destaque: Boolean,
    val visualizacoes: Int,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class NewsResponse(
    val news: List<ApiNews>,
    val totalPages: Int,
    val currentPage: Int,
    val total: Int
)

data class CreateNewsRequest(
    val titulo: String,
    val descricao: String,
    val conteudo: String,
    val data: String,
    val imageUrl: String? = null,
    val categoria: String = "Geral",
    val autor: String = "RepairCars Oficina",
    val tags: List<String> = emptyList(),
    val destaque: Boolean = false
)