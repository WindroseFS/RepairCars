package com.thorapps.repaircars.contacts

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: String = "",
    val name: String = "",
    val phone: String? = null,
    val email: String = ""
) : Parcelable