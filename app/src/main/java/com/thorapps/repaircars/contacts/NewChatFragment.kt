package com.thorapps.repaircars.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.thorapps.repaircars.databinding.FragmentNewChatBinding

class NewChatFragment : Fragment() {

    private var _binding: FragmentNewChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSaveContact.setOnClickListener {
            saveContact()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun saveContact() {
        val name = binding.etContactName.text.toString().trim()
        val phone = binding.etContactPhone.text.toString().trim()
        val email = binding.etContactEmail.text.toString().trim()

        if (!validateInputs(name, phone)) {
            return
        }

        val newContact = Contact(
            id = System.currentTimeMillis().toString(),
            name = name,
            phone = if (phone.isNotEmpty()) phone else null,
            email = email
        )

        // SAFE ARGS - Navegando com argumentos
        val action = NewChatFragmentDirections.actionNewChatFragmentToContactsFragment(
            newContact = newContact
        )
        findNavController().navigate(action)

        showSuccess("Contato adicionado com sucesso!")
    }

    private fun validateInputs(name: String, phone: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.etContactName.error = "Digite o nome do contato"
            isValid = false
        } else {
            binding.etContactName.error = null
        }

        if (phone.isEmpty()) {
            binding.etContactPhone.error = "Digite o telefone do contato"
            isValid = false
        } else {
            binding.etContactPhone.error = null
        }

        return isValid
    }

    private fun showSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}