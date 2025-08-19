package com.thorapps.repaircars

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.databinding.ActivityDatabaseViewerBinding

class DatabaseViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDatabaseViewerBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatabaseViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        // Mostrar todos os dados do banco
        val contacts = dbHelper.getAllContacts()
        val allMessages = contacts.flatMap { dbHelper.getMessagesForContact(it.id) }

        binding.tvDatabaseInfo.text = buildString {
            append("=== CONTATOS ===\n\n")
            contacts.forEach { contact ->
                append("ID: ${contact.id}\n")
                append("Nome: ${contact.name}\n")
                append("Última mensagem: ${contact.lastMessage}\n")
                append("Timestamp: ${contact.timestamp}\n\n")
            }

            append("\n=== MENSAGENS ===\n\n")
            allMessages.forEach { message ->
                append("ID: ${message.id}\n")
                append("Contato ID: ${message.contactId}\n")
                append("Mensagem: ${message.text}\n")
                append("Enviada: ${message.isSent}\n")
                append("Timestamp: ${message.timestamp}\n\n")
            }
        }
    }
}