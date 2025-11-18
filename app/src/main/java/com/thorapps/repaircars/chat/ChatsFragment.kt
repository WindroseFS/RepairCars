package com.thorapps.repaircars.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thorapps.repaircars.database.DatabaseHelper
import com.thorapps.repaircars.databinding.FragmentChatsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseHelper = DatabaseHelper(requireContext())

        setupRecyclerView()
        setupClickListeners()
        loadChats()
    }

    private fun setupRecyclerView() {
        chatsAdapter = ChatsAdapter { contactId, contactName ->
            navigateToChat(contactId ?: "", contactName ?: "Contato desconhecido")
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

    private fun loadChats() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val contactsWithMessages = databaseHelper.getContactsWithLastMessage()

                Log.d("ChatsFragment", "Encontrados ${contactsWithMessages.size} contatos com mensagens")

                if (contactsWithMessages.isEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        showEmptyState()
                    }
                } else {
                    val chats = contactsWithMessages.map { contactDisplay ->
                        Chat(
                            contactId = contactDisplay.id,
                            contactName = contactDisplay.name,
                            lastMessage = contactDisplay.lastMessage ?: "Sem mensagens",
                            timestamp = System.currentTimeMillis(),
                            unreadCount = contactDisplay.unreadCount
                        )
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        hideEmptyState()
                        chatsAdapter.submitList(chats)
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatsFragment", "Erro ao carregar chats: ${e.message}")
                CoroutineScope(Dispatchers.Main).launch {
                    showEmptyState()
                }
            }
        }
    }

    private fun showEmptyState() {
        binding.recyclerViewChats.visibility = View.GONE
        binding.textEmptyState.visibility = View.VISIBLE
        binding.textEmptyState.text = "Nenhuma conversa iniciada\nToque no botão + para começar uma nova conversa"
    }

    private fun hideEmptyState() {
        binding.recyclerViewChats.visibility = View.VISIBLE
        binding.textEmptyState.visibility = View.GONE
    }

    private fun navigateToChat(contactId: String, contactName: String) {
        Log.d("ChatsFragment", "Navegando para chat: $contactName ($contactId)")
        val action = ChatsFragmentDirections.actionChatsFragmentToChatFragment(
            contactId = contactId,
            contactName = contactName
        )
        findNavController().navigate(action)
    }

    private fun navigateToContacts() {
        val action = ChatsFragmentDirections.actionChatsFragmentToContactsFragment()
        findNavController().navigate(action)
    }

    override fun onResume() {
        super.onResume()
        loadChats()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}