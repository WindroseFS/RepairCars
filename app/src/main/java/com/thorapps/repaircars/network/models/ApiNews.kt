package com.thorapps.repaircars.network.models

import com.google.gson.annotations.SerializedName

data class ApiNews(
    @SerializedName("_id") val id: String,
    val titulo: String,
    val descricao: String,
    val conteudo: String, // ✅ ADICIONADO
    val categoria: String,
    val data: String,
    val dataPublicacao: String?, // ✅ ADICIONADO
    val imageUrl: String?,
    val visualizacoes: Int,
    val autor: String?, // ✅ ADICIONADO
    val tags: List<String>?, // ✅ ADICIONADO
    val destaque: Boolean? // ✅ ADICIONADO
)

data class NewsResponse(
    val news: List<ApiNews>,
    val total: Int,
    val page: Int,
    val limit: Int
)