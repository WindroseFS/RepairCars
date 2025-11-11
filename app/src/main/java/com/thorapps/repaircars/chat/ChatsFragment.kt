package com.thorapps.repaircars.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.databinding.FragmentChatsBinding

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatsAdapter: ChatsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        loadSampleChats()
    }

    private fun setupRecyclerView() {
        // Configurar o RecyclerView com o ID correto do seu layout
        chatsAdapter = ChatsAdapter { contactId, contactName ->
            navigateToChat(contactId, contactName)
        }

        binding.recyclerViewChats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatsAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.fabNewChat.setOnClickListener {
            navigateToContacts()
        }
    }

    private fun loadSampleChats() {
        val sampleChats = listOf(
            Chat(
                contactId = "1",
                contactName = "Oficina Central",
                lastMessage = "Olá! Como posso ajudar com seu veículo?",
                timestamp = System.currentTimeMillis() - 300000, // 5 minutos atrás
                unreadCount = 2
            ),
            Chat(
                contactId = "2",
                contactName = "Suporte Técnico",
                lastMessage = "Seu orçamento está pronto para revisão",
                timestamp = System.currentTimeMillis() - 3600000, // 1 hora atrás
                unreadCount = 1
            ),
            Chat(
                contactId = "3",
                contactName = "Mecânico João",
                lastMessage = "As peças chegaram, podemos agendar?",
                timestamp = System.currentTimeMillis() - 86400000, // 1 dia atrás
                unreadCount = 0
            ),
            Chat(
                contactId = "4",
                contactName = "Atendimento",
                lastMessage = "Lembramos que sua revisão está agendada",
                timestamp = System.currentTimeMillis() - 172800000, // 2 dias atrás
                unreadCount = 0
            ),
            Chat(
                contactId = "5",
                contactName = "Gerente Carlos",
                lastMessage = "Temos uma promoção especial para clientes",
                timestamp = System.currentTimeMillis() - 259200000, // 3 dias atrás
                unreadCount = 0
            )
        )

        chatsAdapter.submitList(sampleChats)
    }

    private fun navigateToChat(contactId: String, contactName: String) {
        val action = ChatsFragmentDirections.actionChatsFragmentToChatFragment(
            contactId = contactId,
            contactName = contactName
        )
        findNavController().navigate(action)
    }

    private fun navigateToContacts() {
        val action = ChatsFragmentDirections.actionChatsFragmentToContactsFragment(
            newContact = null
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}