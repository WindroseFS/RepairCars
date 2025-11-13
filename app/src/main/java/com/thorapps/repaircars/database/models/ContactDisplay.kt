package com.thorapps.repaircars.database.models

import com.thorapps.repaircars.contacts.Contact

data class ContactDisplay(
    val contact: Contact,
    val lastMessage: String? = null
) {
    val phone: String? = contact.phone
    val email: String = contact.email ?: ""
}