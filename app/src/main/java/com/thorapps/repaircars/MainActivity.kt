package com.thorapps.repaircars

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.thorapps.repaircars.database.ContactsAdapter
import com.thorapps.repaircars.database.DatabaseHelper
import com.thorapps.repaircars.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        setupToolbarAndDrawer()
        initializeViews()
        setupClickListeners()
        loadContacts()

        // ✅ Novo método: registra callback para o botão “voltar”
        onBackPressedDispatcher.addCallback(this) {
            handleOnBackPressed()
        }
    }

    /** Configura a Toolbar e o Drawer (menu lateral) */
    private fun setupToolbarAndDrawer() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    /** Inicializa RecyclerView e Adapter */
    private fun initializeViews() {
        contactsAdapter = ContactsAdapter(emptyList()) { contact ->
            openChat(contact.id, contact.name)
        }

        binding.contactsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = contactsAdapter
            setHasFixedSize(true)
        }
    }

    /** Clique nos botões */
    private fun setupClickListeners() {
        binding.btnNewChat.setOnClickListener {
            navigateToNewChat()
        }
    }

    /** Ações do menu lateral */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_chat -> {
                Toast.makeText(this, "Você já está na tela de Chat", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_news -> {
                startActivity(Intent(this, NewsActivity::class.java))
            }

            R.id.nav_logout -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

            R.id.nav_exit -> {
                finishAffinity()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /** Carrega os contatos no RecyclerView */
    private fun loadContacts() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                showLoading(true)
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
            } finally {
                showLoading(false)
            }
        }
    }

    /** Abre um chat existente */
    private fun openChat(contactId: Long, contactName: String) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("CONTACT_ID", contactId)
            putExtra("CONTACT_NAME", contactName)
        }
        startActivity(intent)
    }

    /** Abre tela para criar novo chat */
    private fun navigateToNewChat() {
        val intent = Intent(this, NewChatActivity::class.java)
        startActivity(intent)
    }

    /** Controla visibilidade do carregamento */
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility =
            if (show) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnNewChat.isEnabled = !show
    }

    /** Mostra ou oculta estado vazio */
    private fun showEmptyState(show: Boolean) {
        binding.tvEmptyState.visibility =
            if (show) android.view.View.VISIBLE else android.view.View.GONE
        binding.contactsRecyclerView.visibility =
            if (show) android.view.View.GONE else android.view.View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    @Suppress("unused")
    private fun handleOnBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadContacts()
    }
}
