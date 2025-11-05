package com.thorapps.repaircars.contacts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.NewChatActivity
import com.thorapps.repaircars.R
import com.thorapps.repaircars.chats.ChatsFragment
import com.thorapps.repaircars.databinding.FragmentContactsBinding
import kotlinx.parcelize.Parcelize

class ContactsFragment : Fragment() {
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private val contactList = mutableListOf<Contact>()
    private lateinit var adapter: ContactsAdapter

    // Registrar para receber resultado da NewChatActivity
    private val newContactLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newContact = result.data?.getParcelableExtra<Contact>("NEW_CONTACT")
            newContact?.let { contact ->
                // Adicionar o novo contato à lista
                contactList.add(contact)
                adapter.notifyItemInserted(contactList.size - 1)
                updateEmptyState()

                // Salvar em SharedPreferences
                saveContactsToSharedPrefs()

                showSuccessMessage("Contato adicionado com sucesso!")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        loadContactsFromSharedPrefs()
    }

    private fun setupRecyclerView() {
        adapter = ContactsAdapter(contactList) { contact ->
            openChatWithContact(contact)
        }
        binding.recycleViewContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recycleViewContacts.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAddContact.setOnClickListener {
            // Abrir NewChatActivity e esperar resultado
            val intent = Intent(requireContext(), NewChatActivity::class.java)
            newContactLauncher.launch(intent)
        }
    }

    private fun openChatWithContact(contact: Contact) {
        // Encontrar o ChatsFragment e adicionar novo chat
        findChatsFragment()?.addNewChat(contact.name, contact.phone ?: "", contact.id)

        // Abrir chat com contato selecionado
        val intent = Intent(requireContext(), com.thorapps.repaircars.ChatActivity::class.java).apply {
            putExtra("CONTACT_NAME", contact.name)
            putExtra("CONTACT_PHONE", contact.phone ?: "")
            putExtra("CONTACT_ID", contact.id)
            putExtra("CONTACT_EMAIL", contact.email)
        }
        startActivity(intent)
    }

    private fun findChatsFragment(): ChatsFragment? {
        return try {
            // Tentar encontrar o ChatsFragment através do Navigation Component
            parentFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments
                ?.firstOrNull { it is ChatsFragment } as? ChatsFragment
        } catch (e: Exception) {
            null
        }
    }

    private fun updateEmptyState() {
        if (contactList.isEmpty()) {
            binding.textEmptyState.visibility = View.VISIBLE
            binding.recycleViewContacts.visibility = View.GONE
        } else {
            binding.textEmptyState.visibility = View.GONE
            binding.recycleViewContacts.visibility = View.VISIBLE
        }
    }

    private fun saveContactsToSharedPrefs() {
        try {
            val sharedPref = requireContext().getSharedPreferences("contacts", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()

            // Converter lista para JSON (formato simples)
            val contactsJson = contactList.joinToString(";;") { contact ->
                "${contact.id},${contact.name},${contact.phone ?: ""},${contact.email}"
            }
            editor.putString("contact_list", contactsJson)
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadContactsFromSharedPrefs() {
        try {
            val sharedPref = requireContext().getSharedPreferences("contacts", Context.MODE_PRIVATE)
            val contactsJson = sharedPref.getString("contact_list", "")

            if (!contactsJson.isNullOrEmpty()) {
                contactList.clear()
                contactsJson.split(";;").forEach { contactStr ->
                    val parts = contactStr.split(",")
                    if (parts.size >= 4) {
                        val contact = Contact(
                            id = parts.getOrElse(0) { System.currentTimeMillis().toString() },
                            name = parts.getOrElse(1) { "" },
                            phone = if (parts.getOrElse(2) { "" }.isNotEmpty()) parts[2] else null,
                            email = parts.getOrElse(3) { "" }
                        )
                        // Agora valida nome E email (ambos obrigatórios)
                        if (contact.name.isNotEmpty() && contact.email.isNotEmpty()) {
                            contactList.add(contact)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
            updateEmptyState()
        } catch (e: Exception) {
            e.printStackTrace()
            showErrorMessage("Erro ao carregar contatos")
        }
    }

    private fun showSuccessMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun showErrorMessage(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_LONG).show()
    }

    // Método público para adicionar contato externamente
    fun addContactFromExternal(name: String, email: String, phone: String? = null) {
        val newContact = Contact(
            id = System.currentTimeMillis().toString(),
            name = name,
            phone = phone,
            email = email
        )

        // Verificar se o contato já existe (agora por email, já que é obrigatório)
        val existingContact = contactList.find { it.email == email }
        if (existingContact == null) {
            contactList.add(newContact)
            adapter.notifyItemInserted(contactList.size - 1)
            updateEmptyState()
            saveContactsToSharedPrefs()
            showSuccessMessage("Contato adicionado: $name")
        } else {
            showErrorMessage("Contato já existe na lista")
        }
    }

    // Método para buscar contato por telefone
    fun findContactsByPhone(phone: String): List<Contact> {
        return contactList.filter { it.phone == phone }
    }

    // Método para buscar contato por email (agora principal, já que é obrigatório)
    fun findContactByEmail(email: String): Contact? {
        return contactList.find { it.email == email }
    }

    // Método para buscar contato por nome
    fun findContactsByName(name: String): List<Contact> {
        return contactList.filter { it.name.contains(name, ignoreCase = true) }
    }

    // Método para obter todos os contatos
    fun getAllContacts(): List<Contact> {
        return contactList.toList()
    }

    // Método para limpar todos os contatos (útil para debug)
    fun clearAllContacts() {
        contactList.clear()
        adapter.notifyDataSetChanged()
        updateEmptyState()
        saveContactsToSharedPrefs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}