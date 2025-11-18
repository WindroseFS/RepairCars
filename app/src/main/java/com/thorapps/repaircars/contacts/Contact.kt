package com.thorapps.repaircars.contacts

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: String,
    val name: String,
    val phone: String?,
    val email: String
) : Parcelable {
    // Propriedade adicional que não faz parte do construtor principal
    val lastMessage: String
        get() = "" // Ou você pode fazer uma lógica para obter a última mensagem
}