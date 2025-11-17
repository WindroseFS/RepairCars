package com.thorapps.repaircars.database.models

import com.thorapps.repaircars.contacts.Contact

data class ContactDisplay(
    val id: String,
    val name: String,
    val phone: String,
    val email: String?,
    val type: String = "customer",
    val lastMessage: String? = null,
    val lastMessageTime: String? = null,
    val unreadCount: Int = 0
) {
    companion object {
        fun fromContact(contact: Contact, lastMessage: String? = null, lastMessageTime: String? = null, unreadCount: Int = 0): ContactDisplay {
            return ContactDisplay(
                id = contact.id ?: "",
                name = contact.name,
                phone = contact.phone,
                email = contact.email,
                lastMessage = lastMessage,
                lastMessageTime = lastMessageTime,
                unreadCount = unreadCount
            )
        }
    }

    val displayPhone: String get() = phone
    val displayEmail: String get() = email ?: ""
}