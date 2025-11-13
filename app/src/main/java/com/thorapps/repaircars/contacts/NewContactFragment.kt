package com.thorapps.repaircars.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        var hasError = false

        // ✅ Nome obrigatório
        if (name.isEmpty()) {
            binding.etContactName.error = "Nome é obrigatório"
            hasError = true
        }

        // ✅ E-mail obrigatório
        if (email.isEmpty()) {
            binding.etContactEmail.error = "E-mail é obrigatório"
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etContactEmail.error = "Formato de e-mail inválido"
            hasError = true
        }

        // ❌ Telefone opcional — sem erro se estiver vazio

        if (hasError) return

        val contactId = databaseHelper.generateContactId()

        CoroutineScope(Dispatchers.IO).launch {
            val result = databaseHelper.addContact(
                id = contactId,
                name = name,
                phone = if (phone.isEmpty()) null else phone,
                email = email
            )

            CoroutineScope(Dispatchers.Main).launch {
                if (result != -1L) {
                    Toast.makeText(requireContext(), "Contato salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar contato", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
