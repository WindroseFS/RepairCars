package com.thorapps.repaircars.contacts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.database.DatabaseHelper
import com.thorapps.repaircars.databinding.FragmentContactsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactsAdapter: ContactAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())

        setupRecyclerView()
        setupFab()
        loadContacts()
    }

    private fun setupRecyclerView() {
        contactsAdapter = ContactAdapter { contact ->
            Log.d("ContactsFragment", "Contato selecionado: ${contact.name} (${contact.id})")

            val detailFragment = ContactDetailFragment.newInstance(contact)
            detailFragment.show(parentFragmentManager, "ContactDetail")
        }

        binding.contactsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactsAdapter
        }
    }

    private fun setupFab() {
        binding.fabAddContact.setOnClickListener {
            val action = ContactsFragmentDirections.actionContactsFragmentToNewChatFragment()
            findNavController().navigate(action)
        }
    }

    private fun loadContacts() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val contacts = databaseHelper.getAllContacts()

                Log.d("ContactsFragment", "Carregados ${contacts.size} contatos")

                CoroutineScope(Dispatchers.Main).launch {
                    if (contacts.isEmpty()) {
                        showEmptyState()
                    } else {
                        hideEmptyState()
                        contactsAdapter.updateContacts(contacts)
                    }
                }
            } catch (e: Exception) {
                Log.e("ContactsFragment", "Erro ao carregar contatos: ${e.message}")
                CoroutineScope(Dispatchers.Main).launch {
                    showEmptyState()
                }
            }
        }
    }

    private fun showEmptyState() {
        binding.contactsRecyclerView.visibility = View.GONE
        binding.textEmptyState.visibility = View.VISIBLE
        binding.textEmptyState.text = "Nenhum contato encontrado\nToque no botão + para adicionar um novo contato"
    }

    private fun hideEmptyState() {
        binding.contactsRecyclerView.visibility = View.VISIBLE
        binding.textEmptyState.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        loadContacts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}