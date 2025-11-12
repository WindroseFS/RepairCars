package com.thorapps.repaircars.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.thorapps.repaircars.database.DatabaseHelper
import com.thorapps.repaircars.databinding.FragmentNewContactBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewContactFragment : Fragment() {

    private var _binding: FragmentNewContactBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSaveContact.setOnClickListener {
            saveContact()
        }
    }

    private fun saveContact() {
        val name = binding.etContactName.text.toString().trim()
        val phone = binding.etContactPhone.text.toString().trim()
        val email = binding.etContactEmail.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty()) {
            if (name.isEmpty()) {
                binding.etContactName.error = "Nome é obrigatório"
            }
            if (phone.isEmpty()) {
                binding.etContactPhone.error = "Telefone é obrigatório"
            }
            return
        }

        // Gera ID único para o contato
        val contactId = databaseHelper.generateContactId()

        // Salva no banco de dados
        CoroutineScope(Dispatchers.IO).launch {
            val result = databaseHelper.addContact(contactId, name, phone, email)

            CoroutineScope(Dispatchers.Main).launch {
                if (result != -1L) {
                    // Navega de volta para ContactsFragment
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}