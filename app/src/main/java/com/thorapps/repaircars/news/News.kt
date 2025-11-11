package com.thorapps.repaircars.news

// CORREÇÃO: Data class com propriedades corretas
data class News(
    val titulo: String,
    val descricao: String,
    val data: String,
    val imageUrl: String? = null // Parâmetro opcional com valor padrão
)