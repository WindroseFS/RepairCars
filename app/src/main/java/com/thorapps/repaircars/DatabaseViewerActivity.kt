package com.thorapps.repaircars

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.databinding.ActivityDatabaseViewerBinding

class DatabaseViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatabaseViewerBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var contactsAdapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatabaseViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        // Recupera contatos do banco e converte para ContactDisplay
        val contactsDisplay = dbHelper.getAllContacts().map { contact ->
            ContactDisplay(
                id = contact.id,
                name = contact.name,
                lastMessage = "" // opcional, pois aqui só exibimos contatos
            )
        }

        // Adapter sem ação de clique
        contactsAdapter = ContactsAdapter(contactsDisplay) { contact ->
            // Se quiser adicionar ação ao clicar, coloque aqui
            // Por exemplo, abrir uma tela de detalhes do contato
        }

        // Configura RecyclerView
        binding.recyclerViewContacts.apply {
            layoutManager = LinearLayoutManager(this@DatabaseViewerActivity)
            adapter = contactsAdapter
            setHasFixedSize(true)
        }
    }
}
