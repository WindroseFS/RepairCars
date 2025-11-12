package com.thorapps.repaircars.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.thorapps.repaircars.databinding.FragmentNewContactBinding

class NewContactFragment : Fragment() {

    private var _binding: FragmentNewContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            // ✅ Navega de volta para ContactsFragment
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
            // Mostrar erro se campos obrigatórios estiverem vazios
            if (name.isEmpty()) {
                binding.etContactName.error = "Nome é obrigatório"
            }
            if (phone.isEmpty()) {
                binding.etContactPhone.error = "Telefone é obrigatório"
            }
            return
        }

        // Criar novo contato
        val newContact = Contact(
            id = System.currentTimeMillis().toString(),
            name = name,
            phone = phone,
            email = if (email.isNotEmpty()) email else null
        )

        // ✅ Navegar de volta para ContactsFragment passando o novo contato
        val action = NewContactFragmentDirections.actionNewChatFragmentToContactsFragment(
            newContact = newContact
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}