package com.thorapps.repaircars.database.models

import com.thorapps.repaircars.contacts.Contact

data class ContactDisplay(
    val contact: Contact,
    val lastMessage: String? = null
) {
    val phone: String? get() = contact.phone
    val email: String get() = contact.email ?: ""
}