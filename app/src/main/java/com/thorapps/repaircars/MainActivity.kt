package com.thorapps.repaircars

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.databinding.ActivityMainBinding
import com.thorapps.repaircars.database.DatabaseHelper
import com.thorapps.repaircars.database.ContactsAdapter
import com.thorapps.repaircars.ui.DatabaseViewerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var contactsAdapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)

        initializeViews()
        setupClickListeners()
        loadContacts()
    }

    private fun initializeViews() {
        setupRecyclerView()
    }

    private fun setupClickListeners() {
        // Botão Chat - para visualizar chats existentes
        binding.btnChat.setOnClickListener {
            // Já estamos na tela principal de chats
            Toast.makeText(this, "Visualizando chats", Toast.LENGTH_SHORT).show()
        }

        // Botão Novo Chat - para criar novo contato/chat
        binding.btnNewChat.setOnClickListener {
            navigateToNewChat()
        }

        // Botão Ver BD - para visualizar banco de dados
        binding.btnViewDatabase.setOnClickListener {
            navigateToDatabaseViewer()
        }

        // Botão Sair - finaliza a aplicação
        binding.btnLogout.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        contactsAdapter = ContactsAdapter(emptyList()) { contact ->
            openChat(contact.id, contact.name)
        }

        binding.contactsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = contactsAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadContacts() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                showLoading(true)
                showEmptyState(false)

                val contacts = withContext(Dispatchers.IO) {
                    dbHelper.getContactsWithLastMessage()
                }

                if (contacts.isEmpty()) {
                    showEmptyState(true)
                    contactsAdapter.updateContacts(emptyList())
                } else {
                    showEmptyState(false)
                    contactsAdapter.updateContacts(contacts)
                }

            } catch (e: Exception) {
                showError("Erro ao carregar contatos: ${e.message}")
                showEmptyState(true)
                e.printStackTrace()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun openChat(contactId: Long, contactName: String) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("CONTACT_ID", contactId)
            putExtra("CONTACT_NAME", contactName)
        }
        startActivity(intent)
    }

    private fun navigateToNewChat() {
        val intent = Intent(this, NewChatActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToDatabaseViewer() {
        val intent = Intent(this, DatabaseViewerActivity::class.java)
        startActivity(intent)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }

        // Desabilitar botões durante o loading
        binding.btnNewChat.isEnabled = !show
        binding.btnChat.isEnabled = !show
        binding.btnViewDatabase.isEnabled = !show
        binding.btnLogout.isEnabled = !show
    }

    private fun showEmptyState(show: Boolean) {
        binding.tvEmptyState.visibility = if (show) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }
        binding.contactsRecyclerView.visibility = if (show) {
            android.view.View.GONE
        } else {
            android.view.View.VISIBLE
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        // Recarregar contatos quando a activity retornar ao foco
        loadContacts()
    }
}