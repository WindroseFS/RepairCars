package com.thorapps.repaircars.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactData(
    @PrimaryKey
    val id: String,
    val name: String,
    val phone: String,
    val email: String?,
    val type: String,
    val street: String?,
    val city: String?,
    val state: String?,
    val zipCode: String?,
    val notes: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)