package com.thorapps.repaircars.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.thorapps.repaircars.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val args: ChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Usar os argumentos recebidos
        val contactId = args.contactId
        val contactName = args.contactName

        binding.toolbar.title = contactName

        // Configurar botão de voltar na toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupChat(contactId, contactName)
    }

    private fun setupChat(contactId: Long, contactName: String) {
        // Configurar o chat aqui
        // Exemplo: carregar mensagens, configurar RecyclerView, etc.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}