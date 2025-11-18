package com.thorapps.repaircars.network.models

import com.google.gson.annotations.SerializedName

data class ApiNews(
    val id: String,
    val titulo: String,
    val descricao: String,
    val categoria: String,
    val data: String,
    val imageUrl: String?,
    val visualizacoes: Int
)

data class NewsResponse(
    val news: List<ApiNews>,
    val total: Int,
    val page: Int,
    val limit: Int
)