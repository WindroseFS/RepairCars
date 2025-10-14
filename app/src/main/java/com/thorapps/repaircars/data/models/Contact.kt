package com.thorapps.repaircars.data.models

data class Contact(
    val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val lastMessage: String = ""
)