package com.thorapps.repaircars.network.models

data class CreateNewsRequest(
    val titulo: String,
    val descricao: String,
    val conteudo: String, // ✅ ADICIONADO
    val categoria: String,
    val imageUrl: String? = null,
    val autor: String? = "RepairCars Oficina", // ✅ ADICIONADO
    val tags: List<String>? = emptyList(), // ✅ ADICIONADO
    val destaque: Boolean? = false // ✅ ADICIONADO
)