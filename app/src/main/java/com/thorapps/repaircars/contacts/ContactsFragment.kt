package com.thorapps.repaircars.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thorapps.repaircars.databinding.FragmentContactsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadContacts()
    }

    private fun setupRecyclerView() {
        recyclerView = binding.contactsRecyclerView

        contactsAdapter = ContactsAdapter(emptyList()) { contact ->
            onContactClick(contact)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactsAdapter
        }
    }

    private fun loadContacts() {
        CoroutineScope(Dispatchers.IO).launch {
            // Carregar contatos do banco de dados ou fonte de dados
            // val contacts = databaseHelper.getAllContacts()

            // Exemplo com dados mock
            val mockContacts = listOf(
                Contact("1", "João Silva", "11999999999", "joao@email.com", "Olá, preciso de ajuda"),
                Contact("2", "Maria Santos", "11888888888", "maria@email.com", "Orçamento por favor"),
                Contact("3", "Carlos Oliveira", "11777777777", "carlos@email.com", "Preciso trocar o óleo"),
                Contact("4", "Ana Costa", "11666666666", "ana@email.com", "Revisão completa")
            )

            CoroutineScope(Dispatchers.Main).launch {
                contactsAdapter.updateContacts(mockContacts)
            }
        }
    }

    private fun onContactClick(contact: Contact) {
        Toast.makeText(requireContext(), "Abrir chat com ${contact.name}", Toast.LENGTH_SHORT).show()

        // Exemplo de navegação (descomente quando tiver o navigation graph configurado)
        /*
        val action = ContactsFragmentDirections.actionContactsFragmentToChatFragment(
            contactId = contact.id.toLong(),
            contactName = contact.name
        )
        findNavController().navigate(action)
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}