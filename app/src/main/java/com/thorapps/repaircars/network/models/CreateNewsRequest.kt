package com.thorapps.repaircars.network.models

data class CreateNewsRequest(
    val titulo: String,
    val descricao: String,
    val categoria: String,
    val imageUrl: String? = null
)