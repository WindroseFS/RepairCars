package com.thorapps.repaircars.models

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String? = "",
    val profileImage: String = "",
    val createdAt: Long = 0L,
    val userType: String = "client" // "client" ou "mechanic"
) {
    // Construtor vazio necessário para Firebase
    constructor() : this("", "", "", "", "", 0L, "client")
}