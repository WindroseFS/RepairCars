package com.thorapps.repaircars.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.thorapps.repaircars.databinding.FragmentContactsBinding
import com.thorapps.repaircars.R

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Exemplo: quando selecionar um contato (ajuste conforme seu layout)
        binding.root.findViewById<View>(R.id.some_contact_view)?.setOnClickListener {
            val contactId = 1L
            val contactName = "Contato Exemplo"

            // Navegar para o chat
            val action = ContactsFragmentDirections.actionContactsFragmentToChatFragment(
                contactId = contactId,
                contactName = contactName
            )
            findNavController().navigate(action)
        }

        // Navegar para novo chat (ajuste conforme seu layout)
        binding.root.findViewById<View>(R.id.fab_new_contact)?.setOnClickListener {
            val action = ContactsFragmentDirections.actionContactsFragmentToNewChatFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}